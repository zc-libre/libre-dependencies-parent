---
paths:
  - "**/*.java"
---

# Java 注释与 Javadoc 规范（对齐 Spring 风格）

编写或修改 Java 代码时遵循以下规则。注释风格严格对齐 Spring 框架与本项目现有写法。

标 `[PMD]` 者由 `mvn verify` 阶段的 PMD/P3C 校验覆盖；最终以 PMD 报告为准。

## 类级 Javadoc

- public 类、接口、注解、枚举**必须**有类级 Javadoc——这是对外 API 的第一手说明。 `[PMD]`
- 格式：中文描述行，空一行后写标签；`@author` 统一填 `libre`：

```java
/**
 * 服务调用不鉴权注解
 *
 * @author libre
 */
public @interface Inner {
}
```

- 标签：`@author` 必填；`@since` 可选；不使用非标准的 `@date` 标签（旧代码遗留，新代码不再写）。

## 方法 Javadoc（Spring 紧凑式）

- public 方法必须有 Javadoc。
- **紧凑式**：描述行之后**直接**接 `@param`/`@return`/`@throws`，中间**不空行**——这是 Spring 与本项目的既有风格，保持一致以免风格割裂：

```java
/**
 * 是否AOP统一处理
 * @return false, true
 */
boolean value() default true;
```

- 每个参数一行 `@param`；有返回值必写 `@return`；抛受检异常写 `@throws`。

## 行内注释

- 用 `//` 解释**为什么（why）**，而非复述代码做了什么（what）——代码本身已说明 what。
- 注释与代码同步更新；删除代码时一并删除其注释，避免注释与实现脱节误导后人。
- **禁止**保留被注释掉的死代码——需要历史就查 git。 `[PMD]`
- 魔法值、特殊业务逻辑、易误解的分支必须加注释说明意图。
- `TODO` / `FIXME` 标注责任人，如 `// TODO(libre): ...`，便于追溯。

## 语言

- 注释一律用中文，与项目现状保持一致。
