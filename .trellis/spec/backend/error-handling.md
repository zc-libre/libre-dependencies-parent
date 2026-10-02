# 错误处理与统一响应

> 异常如何抛出、翻译、返回给客户端。核心分工：`libre-toolkit` 提供框架无关契约（`R`、`ResultCode`、`LibreException`），`libre-boot` 提供 Web 层落地（全局异常处理、`BusinessException`）。

---

## 统一响应体 `R<T>`

所有对外接口统一返回 `org.zclibre.toolkit.result.R<T>`（`libre-toolkit/.../result/R.java`）。

**四字段**：`int code` / `boolean success` / `T data` / `String msg`。`success` 由构造器自动推导（`code == ResultCode.SUCCESS.code`）；所有构造器 `private`，**强制走静态工厂**：

| 场景 | 工厂方法 |
|------|---------|
| 成功携带数据 | `R.data(T)` / `R.data(T, msg)` / `R.data(code, T, msg)` |
| 成功无数据 | `R.success(String)` / `R.success(IResultCode)` / `R.success(IResultCode, msg)` |
| 失败 | `R.fail(String)` / `R.fail(int, msg)` / `R.fail(IResultCode)` / `R.fail(IResultCode, msg)` |
| 布尔转结果 | `R.status(boolean)`（true→success，false→fail） |
| 判定 | `R.isSuccess(R)` / `R.isNotSuccess(R)`（静态） |

默认消息常量在 `LibreConstants`：`DEFAULT_SUCCESS_MESSAGE="操作成功"`、`DEFAULT_NULL_MESSAGE="暂无承载数据"`、`DEFAULT_FAILURE_MESSAGE="操作失败"`。

---

## 错误码：枚举 + 接口，复用 HTTP 语义

不用散落常量，统一 **枚举 + `IResultCode` 接口**。

- `IResultCode`（`libre-toolkit/.../result/IResultCode.java`）：`extends Serializable`，两方法 `int getCode()` / `String getMessage()`。
- `ResultCode`（`libre-toolkit/.../result/ResultCode.java`）：`@Getter @AllArgsConstructor enum ... implements IResultCode`，字段 `(int code, String message)`。code **直接复用 HTTP 语义**（大量用 `HttpServletResponse.SC_*`），使业务码与 HTTP 状态对齐：`SUCCESS(200)`、`FAILURE(400,"业务异常")`（默认失败码）、`UN_AUTHORIZED(401)`、`REQ_REJECT(403)`、`NOT_FOUND(404)`、参数类统一 400（`PARAM_MISS`/`PARAM_TYPE_ERROR`/`PARAM_BIND_ERROR`/`PARAM_VALID_ERROR`/`MSG_NOT_READABLE`）等。

**扩展方式**：下游业务定义自己的枚举 `implements IResultCode`，即可无缝传入 `R.fail(...)` / `new BusinessException(IResultCode)`，无需改动框架。

> ⚠️ 现存待规整项（新增码时勿模仿）：`UN_AUTHORIZED(401)` 与 `UN_INTERFACE_AUTHORIZED(4001)` 语义重复；`MEDIA_TYPE_NOT_SUPPORTED(415)` 被 415 和本应 406 的两个 handler 复用。

---

## 自定义异常

| 类 | 位置 | 继承 | 携带 |
|----|------|------|------|
| `BusinessException` | `libre-boot/.../exception/` | `RuntimeException` | `@Nullable R<?> result` |
| `LibreException` | `libre-toolkit/.../exception/` | `RuntimeException` | 仅 message（支持 `String.format` 变参） |

**`BusinessException` 是业务层抛异常的核心约定**（`libre-boot/.../exception/BusinessException.java`）：

```java
public class BusinessException extends RuntimeException {

	@Nullable
	private final R<?> result;

	public BusinessException(R<?> result)                          { super(result.getMsg()); this.result = result; }
	public BusinessException(IResultCode rCode)                    { this(rCode, rCode.getMessage()); }
	public BusinessException(IResultCode rCode, String message)    { super(message); this.result = R.fail(rCode, message); }
	public BusinessException(String message)                       { super(message); this.result = null; }

	// 关键：不采集堆栈以提高性能（业务异常是预期内的）
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}
```

关键约定：**重写 `fillInStackTrace()` 直接 `return this`**——业务异常是预期内的流程分支，不采集堆栈以提高性能；仅带 cause 的构造器才保留堆栈。`getResult()` 供 `GlobalExceptionTranslator` / `LibreErrorAttributes` 直接输出。

`LibreException` 是工具库内部用的轻量运行时异常，支持格式化消息，无 code 概念。功能模块的领域异常（`RedisLockException`、`Ip2regionException`、`MqttException`）各自继承 `RuntimeException`，放在本模块 `exception/` 子包。

---

## 全局异常处理：两层 `@RestControllerAdvice`

`libre-boot` 用 `@Order` 分两层，均注册在 `libre-boot/.../META-INF/spring/....AutoConfiguration.imports`：

### 第一层：`GlobalExceptionHandler`（HIGHEST_PRECEDENCE，处理具体异常）

`libre-boot/.../exception/GlobalExceptionHandler.java`，类注解：

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@RestControllerAdvice
public class GlobalExceptionHandler {
```

每个 `@ExceptionHandler` 方法都：显式 `@ResponseStatus(...)` 声明 HTTP 状态 → 打一行日志（仅 `e.getMessage()`，不打堆栈）→ 返回 `R.fail(ResultCode.XXX, message)`。覆盖的异常包括 `MissingServletRequestParameterException`(400,`PARAM_MISS`)、`MethodArgumentTypeMismatchException`(400)、`AccessDeniedException`(403)、`MethodArgumentNotValidException`/`BindException`(400,走 `BindingResult`)、`ConstraintViolationException`(400)、`NoHandlerFoundException`(404)、`HttpMessageNotReadableException`(400)、`HttpRequestMethodNotSupportedException`(405)、`HttpMediaTypeNotSupportedException`(415) 等。

```java
@ExceptionHandler(MissingServletRequestParameterException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public R<Object> handleError(MissingServletRequestParameterException e) {
	log.warn("缺少请求参数:{}", e.getMessage());
	String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
	return R.fail(ResultCode.PARAM_MISS, message);
}
```

### 第二层：`GlobalExceptionTranslator`（最低优先级，兜底）

`libre-boot/.../exception/GlobalExceptionTranslator.java`（`@Slf4j` + `@AutoConfiguration` + `@RestControllerAdvice` + `@RequiredArgsConstructor`）只处理两类，均 500 且 **`log.error("...", e)` 带堆栈**：

- `BusinessException` → 取其 `getResult()`；若 result 为 null 则 `R.fail(ResultCode.FAILURE, msg)` 并按未知异常发 `LibreErrorEvent` 事件。
- `Throwable`（一切未捕获异常）→ `R.fail(ResultCode.FAILURE)` 并发 `LibreErrorEvent`（含请求方法/URL/IP/堆栈，供异步监听）。

### Servlet 容器错误兜底（非 controller 抛出）

`LibreErrorAttributes`（继承 `DefaultErrorAttributes`）与 `LibreErrorController`（继承 `BasicErrorController`）把容器级错误也统一渲染成 `{code, msg, success, data}`，与 `R` 结构对齐；由 `LibreErrorAutoConfiguration`（`@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)` + `@ConditionalOnMissingBean`）装配。

---

## 参数校验（JSR-303）

采用标准 `jakarta.validation`。校验失败异常由 `GlobalExceptionHandler` 集中翻译：

- `@RequestBody @Valid` 失败 → `MethodArgumentNotValidException` → 取**第一个** `FieldError`，消息 `"字段名:默认消息"`，返回 `ResultCode.PARAM_BIND_ERROR`。
- 表单绑定失败 → `BindException`（同上 `BindingResult` 逻辑）。
- 方法级/路径参数校验（`@Validated` on class）失败 → `ConstraintViolationException` → 取首个 violation，返回 `ResultCode.PARAM_VALID_ERROR`。

**校验分组**（`libre-toolkit/.../validation/`）：`CreateGroup` / `UpdateGroup` / `GetGroup` / `DeleteGroup`，均 `interface ... extends jakarta.validation.groups.Default`。因 `extends Default`，按某分组校验时**同时触发未标注分组的默认约束**——CRUD 场景常用。库只提供分组接口，`@Validated(CreateGroup.class)` 由下游业务使用。

---

## 约定要点

1. 接口返回一律 `R<T>`，走静态工厂，不 `new`。
2. 业务错误抛 `BusinessException(IResultCode)`，让全局 advice 翻译，**不在 controller 里手写 try-catch 拼响应**。
3. 错误码用枚举 `implements IResultCode`，复用 HTTP 语义。
4. 参数类异常 `log.warn` 不打堆栈；服务端/未知异常 `log.error` 带堆栈（见 [logging-guidelines.md](./logging-guidelines.md)）。
