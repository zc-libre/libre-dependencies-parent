# 日志规约

> Logger 声明、级别语义、占位符风格。全仓已高度统一，新代码严格沿用。

---

## Logger 声明：一律 `@Slf4j`

用 Lombok `@Slf4j` 声明 logger，变量名统一为 `log`（Lombok 生成 `private static final Logger log`）。全仓 28 个文件用 `@Slf4j`，分布在 boot/redisson/mqtt/security/redis/captcha/toolkit。

**禁止**手写 `LoggerFactory.getLogger(...)`。

- 唯一例外：`libre-boot/.../exception/GlobalExceptionHandler.java` 用 `public static final Logger log = LoggerFactory.getLogger(...)`——因需 `public static` 对外/子类暴露，属有意为之。除这种明确需求外一律 `@Slf4j`。
- 反例提醒：标了 `@Slf4j` 却无任何 `log.` 调用（如 `libre-toolkit/.../core/FileUtil.java`）属声明未使用，应删除注解。

---

## 占位符：SLF4J `{}`，禁止字符串拼接

统一用 `{}` 占位符，**不用字符串拼接**；异常对象作为**最后一个参数**传入（不放进占位符，SLF4J 会自动打堆栈）：

```java
// 正确：变量走占位符
log.warn("缺少请求参数:{}", e.getMessage());
log.info("Found @RStreamListener on bean:{} method:{}", beanName, method);

// 正确：异常作最后一个参数（此处 2 个 {} + 末尾 Throwable）
log.error("URL:{} error status:{}", requestUrl, status, error);

// 正确：只打异常
log.error(e.getMessage(), e);
```

细节惯例：占位符前的冒号不加空格（`参数:{}`、`bean:{}`）。业务侧文案多为中文，框架/XSS 侧多为英文。

---

## 级别语义

| 级别 | 使用场景 | 示例 |
|------|---------|------|
| `debug` | 框架内部细节、被过滤/清洗的数据 | XSS 清洗日志 `JacksonXssClean.java` |
| `info` | 组件初始化成功、监听器注册等生命周期事件 | `RStreamSender init success.`、`Found @RStreamListener ...` |
| `warn` | 客户端可预期的错误（缺参、无权限、参数校验失败），**只打 message 不打堆栈** | `GlobalExceptionHandler` 各参数异常、`Oauth2SecurityInnerAspect` 无权限 |
| `error` | 服务端异常、未知异常，**通常带异常对象打堆栈** | `GlobalExceptionTranslator` 的 `log.error("业务异常", e)` / `log.error("未知异常", e)` |

`trace` 全仓未使用。

---

## 要点

1. Logger 一律 `@Slf4j`，变量名 `log`；禁止手写 `LoggerFactory`（除需 `public static` 暴露的基类）。
2. 一律 `{}` 占位符，禁止拼接；异常对象作最后一个参数。
3. 客户端可预期错误 → `warn` 不打堆栈；服务端/未知异常 → `error` 带堆栈。
4. 不记录敏感信息（令牌、密码、密钥）；`libre-toolkit/.../core/DesensitizationUtil.java` 提供脱敏工具供需要时使用。
