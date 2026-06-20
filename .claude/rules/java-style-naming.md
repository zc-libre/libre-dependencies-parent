---
paths:
  - "**/*.java"
---

# Java 代码风格与命名规范

编写或修改 Java 代码时遵循以下规则。每条都是可验证的硬性要求。

标 `[PMD]` 者由 `mvn verify` 阶段的 PMD/P3C 校验覆盖；最终以 PMD 报告（`target/pmd.xml`，规则集见 `.mvn/pmd-ruleset.xml`）为准。

## 格式（唯一权威：spring-javaformat）

- 格式由 `spring-javaformat-maven-plugin` 在 validate 阶段强制，**它是唯一格式权威**——不要手动调整缩进/换行去迎合个人偏好，否则与格式器互相覆盖、徒增 diff。
- Java 源文件用 **Tab 缩进**（见 `.editorconfig`），其余文件用空格。
- 提交前跑 `mvn spring-javaformat:apply` 自动修复格式，否则构建在 validate 阶段失败。
- import 顺序、空行、大括号位置一律交给格式器，不要手动排布。

## 命名

- 类名 `UpperCamelCase`；接口**不加** `I` 前缀。 `[PMD]`
- 抽象类以 `Abstract` 开头；异常类以 `Exception` 结尾——让类型职责从名字即可辨认。 `[PMD]`
- 实现类以 `Impl` 结尾（如 `XxxServiceImpl`）。
- 自动装配类命名 `*AutoConfiguration`；普通配置类 `*Configuration`；配置属性类 `*Properties`。
- 方法名、参数名、局部变量、成员变量用 `lowerCamelCase`。 `[PMD]`
- 常量用 `UPPER_SNAKE_CASE`，且必须 `static final`。 `[PMD]`
- 包名全小写、单词间不加下划线，遵循项目约定 `org.zclibre.<module>`。 `[PMD]`
- 命名用完整英文单词，不用拼音、拼音英文混写，也不用 `a`、`tmp`、`data1` 这类无意义名——名字要自解释含义。
- 布尔类型成员**不要**用 `is` 前缀（用 `deleted` 而非 `isDeleted`），否则部分序列化框架会丢失该字段。 `[PMD]`
- `long`/`Long` 字面量后缀用大写 `L`，不用小写 `l`——小写 `l` 与数字 `1` 极易混淆。 `[PMD]`

## 项目专属约定

- 新增自动装配类后，**必须**把全限定类名登记进
  `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，
  否则 starter 不会被 Spring Boot 装配、功能静默失效。
- 配置类配套一个 `@ConfigurationProperties` 的 `*Properties` 类承载配置项。
- 不要在子模块写死版本号，统一用根 pom 的 `${revision}`。
