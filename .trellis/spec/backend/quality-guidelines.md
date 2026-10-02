# 代码质量与规范

> 强制的代码风格、静态校验、Lombok/工具类惯例、版本与构建约束。提交前必读。

---

## 代码风格：spring-javaformat（强制）

由 `spring-javaformat-maven-plugin` 强制 Spring 官方风格，在 **`validate` 阶段自动校验**，失败会中断构建。

- **Java 源文件用 Tab 缩进**（`.editorconfig`：`[*.java] indent_style = tab`）；其余文件用空格（`json/yml` 缩进 2，其余 4）。
- 提交前跑：`mvn spring-javaformat:apply` 自动修复格式。
- 编码 UTF-8，换行 LF，行尾去空格，文件末尾补空行（`.md` 除外）。

---

## 静态校验：PMD + 阿里 P3C

`maven-pmd-plugin`（钉 PMD 6.x，配 `com.xenoamess.p3c:p3c-pmd`）在 **`verify` 阶段**运行，规则集 `.mvn/pmd-ruleset.xml`，报告落在各模块 `target/pmd.xml`。

- **当前 `pmd.failOnViolation=false`**（`pom.xml:86`，阶段一基线：仅报告不中断）。存量收敛后将改为 `true` 强制中断。**写新代码时按已启用规则自我约束，不要制造新违规。**
- 临时跳过：`-Dpmd.skip=true`。
- 已纳入的 P3C 规则类目（`.mvn/pmd-ruleset.xml`）：命名（禁拼音混用、long 用大写 `L`）、注释（public 类需 author Javadoc、禁注释掉的死代码、魔法值需注释）、常量（禁魔法值）、并发（禁 `Executors` 工厂、`SimpleDateFormat` 静态共享、锁的 try-finally）、异常（禁空 catch、finally 禁 return）、控制语句（必须加大括号、switch 必须 default）、集合（foreach 禁增删）、OOP（包装类 `equals`、常量在前、`BigDecimal` 字符串构造）、日期格式大小写（`YYYY` vs `yyyy`）等。

---

## Lombok 与工具类惯例

Lombok 全工程可用（根 pom 声明为普通依赖）。

- **静态工具类一律 `@UtilityClass`（Lombok）+ `public static` 方法**，禁止手写 `private XxxUtil()` 构造器（`@UtilityClass` 自动设 final、生成私有构造、成员隐式 static）。全仓 17 处，无一手写私有构造。范例 `libre-toolkit/.../core/StringUtil.java`、`ThreadUtil.java`。
- **薄封装第三方工具类用 `extends`**：`StringUtil extends StringUtils`(commons-lang3)、`ClassUtil extends ClassUtils`、`ObjectUtil extends ObjectUtils`(Spring)——对外只暴露一个入口。
- **纯常量集合用 `interface`**（字段天然 `public static final`），如 `CharPool`、`StringPool`、`LibreConstants`。
- Properties 配置类用 `@Data`；装配类注入依赖用 `@RequiredArgsConstructor` + `private final`。
- 遇到 Lombok 已生成但 PMD/编译器误报的弃用/覆盖告警，用 `@SuppressWarnings` 精确抑制（参见近期对 `ClassUtil`/`ObjectUtil`/`JsonUtil` 的 deprecation 抑制提交）。

---

## JSON 统一走 `JsonUtil`

JSON 序列化/反序列化统一用 `libre-toolkit/.../json/JsonUtil.java`（`@UtilityClass`，底层 Jackson）。

- `ObjectMapper` 通过静态内部类 Holder 单例获取（`JsonUtil.getInstance()`），已定制：Locale=CHINA、日期格式 `DatePattern.NORM_DATETIME_PATTERN`、`FAIL_ON_UNKNOWN_PROPERTIES=false`、注册 `JavaTimeModule`、`disable(WRITE_DATES_AS_TIMESTAMPS)`。
- 常用方法：`toJson` / `readValue(Class|TypeReference|JavaType)` / `readList` / `readMap` / `readTree`（均 `public static`）。
- 异常惯例：捕获 `JsonProcessingException`/`IOException` 后统一 `throw Exceptions.unchecked(e)` 转非受检异常（复用 `libre-toolkit/.../core/Exceptions.java`）。**不要自己 new ObjectMapper。**

---

## 版本与构建

- **改版本号只改根 `pom.xml` 的 `<revision>`**（当前 `3.5.10`，与 Spring Boot 对齐），子模块绝不写死版本（`${revision}` + `flatten-maven-plugin` 机制）。
- 新增三方依赖：先在根 pom 的 `<properties>` + `<dependencyManagement>` 加版本管理，子模块只声明 `groupId/artifactId`。
- Java 17（`maven.compiler.source/target=17`）。
- `libre-dependencies` 是对外 BOM，通过 flatten 展开父 pom 的 `dependencyManagement`，使用者 `import` 它统一版本。

### 常用命令

```bash
# 全量构建并安装到本地仓库（默认跳过测试）
mvn clean install

# 只构建某模块及其依赖
mvn install -pl libre-oss -am

# 自动修复代码格式
mvn spring-javaformat:apply

# 发布到 Maven Central（GPG 签名、source/javadoc、central-publishing）
mvn deploy -Prelease
```

---

## 测试

**默认构建跳过测试**（根 pom `maven.test.skip=true` 且 `skipTests=true`）。要实际运行必须显式覆盖两个属性：

```bash
# 运行某模块全部测试
mvn test -pl libre-rabbitmq -Dmaven.test.skip=false -DskipTests=false

# 运行单个测试类 / 方法
mvn test -pl libre-rabbitmq -Dmaven.test.skip=false -DskipTests=false -Dtest=RabbitTest#methodName
```

> 部分模块测试（rabbitmq、redis、mqtt）依赖外部中间件，属集成测试性质。本库为 starter 组件库，无强制单测覆盖率门槛；新增独立可测逻辑（如工具类、纯算法）建议补测试。

---

## Git 提交风格

**gitmoji 前缀 + 中文描述**，例如：`♻️ 将 Springfox 迁移至 springdoc-openapi`、`⬆️ 升级 Spring Boot 版本`、`✨ OssTemplate 增强`。沿用此风格。

---

## 新代码检查清单

- [ ] `mvn spring-javaformat:apply` 已跑，Tab 缩进
- [ ] 无新增 PMD/P3C 违规（命名、魔法值、空 catch、大括号等）
- [ ] 工具类用 `@UtilityClass`，常量集合用 `interface`
- [ ] JSON 走 `JsonUtil`，未自建 ObjectMapper
- [ ] 未在子模块写死版本号，新依赖已在根 pom 管理
- [ ] 提交信息为 gitmoji + 中文
