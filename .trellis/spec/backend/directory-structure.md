# 目录结构与模块组织

> `libre-dependencies` 的代码如何组织：多模块 starter 库的布局约定。

---

## 总览

本仓库是一套发布到 Maven Central 的 **Spring Boot Starter 组件库**（`groupId: org.zclibre`），不是单体 Web 应用。它是 Maven 多模块工程：

- **父工程** `libre-dependencies-parent`（`packaging=pom`）统一管理依赖版本与构建插件。
- **14 个功能模块**：每个都是一个独立的、带自动装配的 starter，使用者按需引入。
- **1 个 BOM** `libre-dependencies`：对外聚合依赖版本，使用者 `import` 它即可统一版本。

依赖方向：`libre-toolkit`（最底层，无自动装配）→ `libre-boot`（依赖 toolkit）→ 其余功能模块按需依赖 toolkit/boot。

---

## 模块清单

| 模块 | 包根 | 职责 |
|------|------|------|
| `libre-toolkit` | `org.zclibre.toolkit` | 基础工具库（`R` 统一响应、JSON、字符串/时间/反射工具、MapStruct 基类、校验分组），多数模块的底层依赖，**不含自动装配** |
| `libre-boot` | `org.zclibre.boot` | Web 应用基座：全局异常处理、统一错误响应、Jackson 配置、WebMvc 配置、XSS 清洗 |
| `libre-dependencies` | — | 对外 BOM（依赖版本聚合），无 Java 源码 |
| `libre-redis` | `org.zclibre.redis` | RedisTemplate/CacheManager 配置、ProtoStuff 序列化、key 过期事件 |
| `libre-redisson` | `org.zclibre.redisson` | Redisson 封装：分布式锁、Stream、RTopic、延迟队列、本地缓存 |
| `libre-mybatis` | `org.zclibre.mybatis` | MyBatis-Plus 自动装配与分页工具（仅 3 个类：`LibreMyBatisAutoConfiguration` / `LibreMyBatisProperties` / `PageUtil`） |
| `libre-oss` | `org.zclibre.oss` | 基于 AWS SDK v2 的对象存储 `OssTemplate`（S3 兼容） |
| `libre-security` | `org.zclibre.security` | OAuth2 授权/资源服务器封装、`@Inner` 内部鉴权、Redis 令牌存储 |
| `libre-captcha` | `org.zclibre.captcha` | 验证码生成与缓存 |
| `libre-swagger` | `org.zclibre.swagger` | springdoc-openapi 自动装配 |
| `libre-ip2region` | `org.zclibre.ip2region` | IP 地理位置查询（含 GraalVM RuntimeHints） |
| `libre-monitor` | `org.zclibre.monitor` | 基于 OSHI 的系统/JVM 监控采集 |
| `libre-mqtt` | `org.zclibre.mqtt` | Spring Integration + Paho 的 `MqttTemplate`、`@MqttListener` |
| `libre-rabbitmq` | `com.libre.rabbitmq` | RabbitMQ 支持（当前仅含测试代码） |

> ⚠️ **包名约定不完全统一**：主流约定为 `org.zclibre.<module>`，但 `libre-rabbitmq` 仍是 `com.libre.rabbitmq`；`libre-mqtt` 的 `@IntegrationComponentScan(basePackages = "com.libre")`（见 `MqttAutoConfiguration.java`）也扫描了旧包名。**新增模块一律用 `org.zclibre.<module>`**。

---

## 单模块内部布局

每个功能模块的标准骨架：

```
libre-<module>/
├── pom.xml
└── src/main/
    ├── java/org/zclibre/<module>/...          # Java 源码
    └── resources/META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

`.imports` 文件是自动装配的注册入口（Spring Boot 2.7+ 机制，**全仓无 `spring.factories`**）——详见 [autoconfiguration-guidelines.md](./autoconfiguration-guidelines.md)。

### 子包命名：三种流派并存（新增代码选流派 1 或 3）

仓库现状没有全局统一的子包命名，写新代码时按模块规模选择：

**流派 1 — `config/` + 功能子包（小/中模块首选）**，如 `libre-oss`、`libre-redis`、`libre-ip2region`：
```
org/zclibre/oss/
├── config/     # OssAutoConfiguration, OssProperties     ← 装配与配置
└── support/    # OssTemplate                             ← 客户端封装
```
```
org/zclibre/ip2region/
├── config/     # Ip2regionConfiguration, Ip2regionProperties, Ip2regionRuntimeHintsRegistrar
├── core/       # Ip2regionSearcher(接口) + 数据结构
├── impl/       # Ip2regionSearcherImpl
├── exception/  # Ip2regionException
└── toolkit/    # IpInfoUtil
```

**流派 3 — 按业务能力横向分包，每包自带 `*Configuration`（大模块首选）**，如 `libre-redisson`：每个功能域一个包，包内自带 `XxxConfiguration` + `XxxProperties` + 实现，并用 `package-info.java` 标注：
```
org/zclibre/redisson/
├── config/   # RedissonConfiguration, RedissonClientConfiguration, DefaultRedisNameResolver
├── lock/     # RedissonLockConfiguration, RedisLockClient(接口)/Impl, RedisLockAspect, @RedisLock, RedisLockProperties
├── stream/   # RedissonStreamConfiguration, ... , package-info
├── topic/    # RedissonRTopicConfiguration, ... , package-info
├── queue/    # RedissonDQConfiguration, ... , package-info
└── command/  # RedissonCommandConfiguration, RedissonUtils
```

**流派 2 — `autoconfigure/` 子包**：仅 `libre-captcha` 使用（把 `CaptchaProperties` 放在 `autoconfigure/` 下）。**新代码不再新增此流派**，统一用 `config/`。

**极简平铺**：`libre-monitor`、`libre-mqtt` 全部类平铺在根包无子包。仅当模块极小（< ~5 个类）时可接受；一旦增长应拆入 `config/` + 功能子包。

---

## 版本与构建机制（改版本号只改一处）

- 所有模块共用同一版本号 `${revision}`（根 `pom.xml`，当前 `3.5.16`，**刻意与 Spring Boot 版本对齐**）。
- 用 `flatten-maven-plugin`（`flattenMode=oss`）做版本占位，构建时生成 `.flattened-pom.xml`（已在 `.gitignore`）。
- **组件版本号统一修改根 pom 的 `<revision>`**；升级 Spring Boot 时同步修改 `<spring-boot.version>`，子模块**绝不写死版本**。
- 第三方依赖版本集中声明在根 pom 的 `<properties>`（`pom.xml:54-89`）与 `<dependencyManagement>`；新增三方库先在此加版本管理，子模块只写 `groupId/artifactId`。

详见 [quality-guidelines.md](./quality-guidelines.md) 的「版本与构建」一节。
