# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 工具指南
### fast-context MCP 工具使用指南

### 核心原则
**任何需要理解代码上下文、探索性搜索、或自然语言定位代码的场景，优先使用 `mcp__fast-context__fast_context_search`**

### 使用场景
### 必须用 fast_context_search
- 探索性搜索（不确定代码在哪个文件/目录）
- 用自然语言描述要找的逻辑（如"XX部署流程"、"XX事件处理"）
- 理解业务逻辑和调用链路
- 跨模块、跨层级查询（如从 router 追到 service 到 model）
- 新任务开始前的代码调研和架构理解
- 中文语义搜索（工具支持中英文双语查询）

### 根据需求选择工具
- **语义搜索 / 不确定位置** → `fast_context_search`（返回文件+行号范围+grep关键词建议）
- **精确关键词搜索** → Grep
- **已知文件路径，查看内容** → Read
- **按文件名模式查找** → Glob
- **编辑已有文件** → Edit

### fast_context_search 参数调优
- `tree_depth=1, max_turns=1` — 快速粗查，适合小项目或初步定位
- `tree_depth=3, max_turns=3`（默认）— 平衡精度与速度，适合大多数场景
- `max_turns=5` — 深度搜索，适合复杂调用链追踪
- `project_path` — 指定搜索的项目根目录，默认为当前工作目录

## 项目概览

`libre-dependencies` 是一套发布到 Maven Central 的 Spring Boot Starter 组件库（`groupId: org.zclibre`），用于简化 Spring Cloud 应用开发。它是一个 Maven 多模块工程：每个功能模块都是一个独立的、带自动装配的 starter，使用者按需引入。

- 所有模块共用同一版本号 `${revision}`（刻意与 Spring Boot 版本对齐）

## 常用命令

> 仓库没有 Maven Wrapper（`mvnw`），使用系统 `mvn`。

```bash
# 全量构建并安装到本地仓库（默认跳过测试）
mvn clean install

# 只构建某个模块及其依赖（-am = also make dependencies）
mvn install -pl libre-oss -am

# 代码格式校验在 validate 阶段自动执行（spring-javaformat），失败会中断构建
# 自动修复格式：
mvn spring-javaformat:apply

# 发布到 Maven Central（启用 GPG 签名、source/javadoc、central-publishing 插件）
mvn deploy -Prelease
```

### 运行测试

默认构建会跳过测试：根 `pom.xml` 中 `maven.test.skip=true` 且 `skipTests=true`。要实际运行测试必须显式覆盖这两个属性：

```bash
# 运行某模块全部测试
mvn test -pl libre-rabbitmq -Dmaven.test.skip=false -DskipTests=false

# 运行单个测试类 / 方法
mvn test -pl libre-rabbitmq -Dmaven.test.skip=false -DskipTests=false -Dtest=RabbitTest
mvn test -pl libre-rabbitmq -Dmaven.test.skip=false -DskipTests=false -Dtest=RabbitTest#methodName
```

注意：部分模块的测试（如 rabbitmq、redis、mqtt）依赖外部中间件，属于集成测试性质。

## 架构与约定

### 版本与发布机制
- 父工程 `libre-dependencies-parent`（packaging=pom）统一管理依赖版本与构建插件。
- 使用 `${revision}` + `flatten-maven-plugin`（`flattenMode=oss`）做版本占位，构建时生成 `.flattened-pom.xml`（已在 `.gitignore` 中）。**改版本号只需改根 pom 的 `<revision>`**，不要在子模块写死版本。
- `libre-dependencies` 模块是给使用者导入的 **BOM**：它通过 flatten 的 `dependencyManagement expand` 把父 pom 的依赖管理展开，使用者 `import` 这个 BOM 即可统一版本。
- 第三方依赖版本集中声明在根 pom 的 `<properties>` 和 `<dependencyManagement>`；新增三方库时优先在此处加版本管理，子模块只声明 `groupId/artifactId`。

### 自动装配（每个模块都是 starter）
每个功能模块通过 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册自动装配类（Spring Boot 2.7+ 的新机制，非 `spring.factories`）。新增自动装配类时必须把全限定类名加进该文件。配置类通常配套一个 `@ConfigurationProperties` 的 `*Properties` 类。

### 包名约定
- 主流约定：`org.zclibre.<module>`（如 `org.zclibre.oss`、`org.zclibre.redis`）。

### 代码风格
- 由 `spring-javaformat-maven-plugin` 强制（Spring 官方风格）。**Java 源文件用 Tab 缩进**（见 `.editorconfig`：`[*.java] indent_style = tab`），其余文件用空格。提交前跑 `mvn spring-javaformat:apply`。
- Lombok 在全工程可用（根 pom 声明为普通依赖）。
- 代码规范 + 阿里 P3C 校验：`mvn install`/`verify` 在 verify 阶段跑 `maven-pmd-plugin`（钉 PMD 6.x，配 `com.xenoamess.p3c:p3c-pmd`），规则集见 `.mvn/pmd-ruleset.xml`，违规报告落在各模块 `target/pmd.xml`。当前 `pmd.failOnViolation=false`（阶段一基线，仅报告不中断）；存量收敛后将根 pom 的该属性改为 `true` 即强制中断。临时跳过：`-Dpmd.skip=true`。

### Git 提交风格
历史提交为 **gitmoji 前缀 + 中文描述**，例如 `♻️ 将 Springfox 迁移至 springdoc-openapi`、`⬆️ 升级 Spring Boot 版本`。沿用此风格。

## 模块清单

| 模块 | 包根 | 职责 |
|------|------|------|
| `libre-toolkit` | `org.zclibre.toolkit` | 基础工具库（`R` 统一响应、JSON、字符串/时间/反射工具、MapStruct 基类、校验分组），多数模块的底层依赖，**不含自动装配** |
| `libre-boot` | `org.zclibre.boot` | Web 应用基座：全局异常处理、统一错误响应、Jackson 配置、WebMvc 配置、XSS 清洗、事务/线程增强 |
| `libre-dependencies` | — | 对外 BOM（依赖版本聚合） |
| `libre-redis` | `org.zclibre.redis` | RedisTemplate/CacheManager 配置、ProtoStuff 序列化、缓存 key 工具、key 过期事件 |
| `libre-redisson` | `org.zclibre.redisson` | Redisson 封装：分布式锁（`@RedisLock` AOP）、Stream、RTopic、延迟队列、本地缓存 |
| `libre-mybatis` | `org.zclibre.mybatis` | MyBatis-Plus 自动装配与分页工具 |
| `libre-oss` | `org.zclibre.oss` | 基于 AWS SDK v2 的对象存储 `OssTemplate`（S3 兼容） |
| `libre-security` | `org.zclibre.security` | Spring Authorization Server / OAuth2 资源服务器封装、`@Inner` 内部接口鉴权、Redis 令牌存储 |
| `libre-captcha` | `org.zclibre.captcha` | 验证码生成与缓存 |
| `libre-swagger` | `org.zclibre.swagger` | springdoc-openapi 自动装配（已从 Springfox 迁移） |
| `libre-ip2region` | `org.zclibre.ip2region` | IP 地理位置查询（含 GraalVM RuntimeHints） |
| `libre-monitor` | `org.zclibre.monitor` | 基于 OSHI 的系统/JVM 监控信息采集 |
| `libre-mqtt` | `org.zclibre.mqtt` | 基于 Spring Integration + Paho 的 MQTT `MqttTemplate`、`@MqttListener` |
| `libre-rabbitmq` | `com.libre.rabbitmq` | RabbitMQ 支持（当前仅含测试代码） |

依赖方向：`libre-toolkit` 为最底层；`libre-boot` 依赖 `libre-toolkit`；其余功能模块按需依赖 toolkit/boot。
