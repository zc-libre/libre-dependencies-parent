---
paths:
  - "**/*.java"
---

# Java 最佳实践（阿里巴巴 P3C 蒸馏）

编写或修改 Java 代码时遵循以下规则。这些是从 P3C 提炼的高频、易违反、且 spring-javaformat 管不到的硬性条款。

标 `[PMD]` 者由 `mvn verify` 阶段的 PMD/P3C 校验覆盖；未标的为强约定（PMD 不一定检得到，仍须遵守）。最终以 PMD 报告（`target/pmd.xml`）为准。

## 并发

- **禁止**用 `Executors` 快捷工厂创建线程池（`newFixedThreadPool` 等），用 `ThreadPoolExecutor` 显式构造并指定队列容量与拒绝策略——快捷工厂的无界队列/线程数易导致 OOM。 `[PMD]`
- `SimpleDateFormat` 线程不安全，**不要**做静态共享；优先用 `java.time.format.DateTimeFormatter`。 `[PMD]`
- 手动创建线程必须指定有意义的线程名，便于排查问题时定位。 `[PMD]`
- 加锁后释放必须放在 `finally` 中，避免异常导致锁泄漏。 `[PMD]`

## 集合

- **禁止**在 foreach 循环里对集合做 `add`/`remove`，会抛 `ConcurrentModificationException`；要删除元素用 `Iterator.remove()` 或 `removeIf`。 `[PMD]`
- `Arrays.asList()` 返回的是定长列表，对其 `add`/`remove` 会抛 `UnsupportedOperationException`。 `[PMD]`
- 遍历 Map 取键值对用 `entrySet()`，不要先 `keySet()` 再逐个 `get()`——后者多一次哈希查找。
- 方法返回集合时返回空集合（`Collections.emptyList()`）而不是 `null`，免去调用方判空。

## 异常

- **禁止**空 `catch` 吞掉异常；至少记录日志或转译后抛出，否则问题被静默掩盖。
- 不要用异常做流程控制（如用异常代替 `if` 判断），异常的栈构建开销远大于条件判断。
- `finally` 块中**禁止** `return`，会吞掉 try 中的异常或返回值。 `[PMD]`
- 防御 NPE：可能为空的返回值用 `Optional`，或显式判空。

## OOP

- 比较包装类的值用 `equals`，**不要**用 `==`——`==` 仅在 `[-128,127]` 缓存区间偶然相等，区间外恒为 false。 `[PMD]`
- `BigDecimal` 用 `String` 构造（`new BigDecimal("0.1")`），禁止 `double` 构造导致精度丢失。 `[PMD]`
- 调 `equals` 时把已知非空的常量放左边：`"x".equals(obj)`，避免 `obj` 为 null 时的 NPE。

## 控制语句

- `if`/`else`/`for`/`while` 即使单行也必须加大括号 `{}`——防止后续插入语句时漏写大括号造成逻辑错误。 `[PMD]`
- `switch` 必须有 `default` 分支；每个 `case` 用 `break`/`return` 收尾，防止意外贯穿。 `[PMD]`
- 避免过深嵌套与过于复杂的条件表达式，复杂条件抽成有名字的布尔变量或方法。 `[PMD]`

## 常量

- **禁止**魔法值（未定义含义的字面量直接出现在代码里），提取为有名常量，让取值意图自解释。 `[PMD]`
