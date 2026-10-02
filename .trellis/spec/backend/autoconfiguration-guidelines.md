# 自动装配规约（每个模块都是 Starter）

> 本库的核心模式：每个功能模块通过 `AutoConfiguration + *Properties + .imports` 组成一个开箱即用的 starter。新增模块或新增装配类时严格遵循本文。

---

## 三件套

一个可用的 starter 由三部分构成：

1. **`XxxAutoConfiguration`** — 用 `@AutoConfiguration` 标注的装配类，定义 `@Bean`。
2. **`XxxProperties`** — `@ConfigurationProperties` 配置类，前缀 `libre.<module>`。
3. **`.imports` 注册文件** — `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，每行一个装配类全限定名。

> ⚠️ **只有写进 `.imports` 的类才会被自动装配触发。** 新增任何 `@AutoConfiguration` 类，必须同步把全限定类名加进该文件，否则不生效。全仓使用 Spring Boot 2.7+ 机制，**不使用旧式 `spring.factories`**。

---

## 1. AutoConfiguration 类

### 基线写法

- 统一用 `@AutoConfiguration`（**不用**旧式 `@Configuration`），配合 `@EnableConfigurationProperties(XxxProperties.class)`。
- 需要注入依赖时用 Lombok `@RequiredArgsConstructor` + `private final` 字段。
- 每个 `@Bean` 普遍加 `@ConditionalOnMissingBean`，允许业务方覆盖。

### 开关型模块（默认开启）

用 `@ConditionalOnProperty` + `matchIfMissing = true`，让模块默认启用、可通过配置关闭。前缀引用 Properties 的 `PREFIX` 常量，避免字符串散落。参见 `libre-oss/.../config/OssAutoConfiguration.java`：

```java
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class OssAutoConfiguration {

	@Bean
	public OssTemplate ossTemplate(OssProperties properties) {
		return new OssTemplate(properties);
	}

}
```

> 若某能力应**默认关闭**，去掉 `matchIfMissing`（如 `libre-redis/.../event/RedisKeyExpiredEventConfiguration.java` 的 `@ConditionalOnProperty(value = "libre.redis.key-expired-event.enable")`）。

### 依赖前置 Bean 的模块

用 `@ConditionalOnBean(核心Client.class)` 保证在核心 Bean 之后装配。`libre-redisson` 的 lock/stream/topic/queue/command 五个子配置**全部**挂在 `RedissonClient` 之后。参见 `libre-redisson/.../lock/RedissonLockConfiguration.java`：

```java
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisLockProperties.class)
public class RedissonLockConfiguration {

	private final RedissonClient client;
	private final RedisNameResolver resolver;

	@Bean
	public RedisLockClient redisLockClient() {
		return new RedisLockClientImpl(client, resolver);
	}

}
```

> `@ConditionalOnBean` 依赖的前置 Bean，其所在配置类必须也在**同一模块的 `.imports`**（或更早的自动配置）里注册。redisson 把 7 个配置类拆分注册即为范例。

### 多 Bean + 逐 Bean 条件

参见 `libre-mqtt/.../MqttAutoConfiguration.java`：几乎每个 `@Bean` 都带 `@ConditionalOnMissingBean`，Integration 相关 Bean 额外带 `@ServiceActivator` 等注解。`libre-captcha/.../autoconfigure/CaptchaAutoConfiguration.java` 则用逐 Bean 的 `@ConditionalOnProperty(name="captcha-type", havingValue="...")` 在同一配置类里按配置分发实例化。

### 控制装配顺序（统一写法）

需要在 Spring Boot 官方自动配置之前/之后装配时：

- ✅ **统一用** `@AutoConfigureBefore(X.class)` / `@AutoConfigureAfter(X.class)`（如 `libre-redis/.../config/RedisTemplateConfiguration.java` 的 `@AutoConfigureBefore(RedisAutoConfiguration.class)`；`libre-boot/.../config/LibreErrorAutoConfiguration.java` 的 `@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)`）。
- ⚠️ 仓库里也存在 `@AutoConfiguration(before = X.class)` 的等价写法（`LibreRedisCacheAutoConfiguration`），但**新代码统一用独立的 `@AutoConfigureBefore` 注解**，保持一致。

### GraalVM / AOT

需要反射/资源提示时加 `@ImportRuntimeHints(XxxRuntimeHintsRegistrar.class)`，参见 `libre-ip2region/.../config/Ip2regionConfiguration.java`。

---

## 2. `*Properties` 配置类

### 约定

- **前缀统一 `libre.<module>`**（`libre.oss` / `libre.redis` / `libre.redisson` / `libre.captcha` / `libre.mqtt` / `libre.ip2region`…）。
- **前缀常量化**：提取 `public static final String PREFIX = "libre.<module>"`，供 AutoConfiguration 引用（目前 oss/ip2region 已提取，其余用字面量——**新代码一律提取 `PREFIX` 常量**）。
- Lombok **统一用 `@Data`**（仓库现存 `@Getter/@Setter` 写法如 redis/ip2region，新代码不再沿用）。
- 字段**就地给默认值**；`enabled` 默认多为 `true`。
- 复杂配置用 `@NestedConfigurationProperty` + 静态内部类组织（如 `RedissonProperties` 的 single/cluster/sentinel 五种模式；`MqttProperties` 的 Retry/Producer/Consumer 内部类）。

```java
@Data
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {

	public static final String PREFIX = "libre.oss";

	private boolean enabled = true;

	private String endpoint;

	private Boolean pathStyleAccess = true;

}
```

---

## 3. Template / 客户端封装类

装配类只负责 `new XxxTemplate(properties)`，资源管理交给封装类本身。

- **持有外部资源（连接、client）的封装类必须实现生命周期接口** `InitializingBean` / `DisposableBean`：在 `afterPropertiesSet()` 里初始化客户端、在 `destroy()` 里 `close()` 释放。范例 `libre-oss/.../support/OssTemplate.java`：

```java
@RequiredArgsConstructor
public class OssTemplate implements InitializingBean, DisposableBean {

	private final OssProperties ossProperties;

	@Override
	public void afterPropertiesSet() {
		// 构建 s3Client / s3AsyncClient / s3Presigner / transferManager
	}

	@Override
	public void destroy() {
		// 依次 close() 释放资源
	}

}
```

- **接口/实现分离时，`@Bean` 返回接口类型**，实现类内部持有底层依赖。范例：`libre-ip2region`（`@Bean Ip2regionSearcher` 返回 `Ip2regionSearcherImpl`）、`libre-mqtt`（`@Bean MqttOptions` 返回 `MqttTemplate`）、`libre-redisson`（`RedisLockClient` 返回 `RedisLockClientImpl`）。

---

## 新增 starter 检查清单

- [ ] `XxxAutoConfiguration` 用 `@AutoConfiguration` + `@EnableConfigurationProperties`
- [ ] `XxxProperties` 前缀 `libre.<module>`，提取 `PREFIX` 常量，`@Data`，字段就地默认值
- [ ] 每个 `@Bean` 视需要加 `@ConditionalOnMissingBean` / `@ConditionalOnProperty` / `@ConditionalOnBean`
- [ ] 全限定类名已写入本模块 `META-INF/spring/....AutoConfiguration.imports`
- [ ] 持有外部资源的封装类实现 `InitializingBean`/`DisposableBean`
- [ ] 装配顺序用独立的 `@AutoConfigureBefore`/`@AutoConfigureAfter` 注解
