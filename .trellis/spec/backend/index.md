# 后端开发规约（libre-dependencies）

> 本目录记录 `libre-dependencies` 组件库的**实际**编码约定，供 AI 助手与团队成员遵循。本库是一套 Spring Boot Starter 组件库（`org.zclibre`），非单体 Web 应用。

---

## 规约索引

| 文档 | 内容 |
|------|------|
| [目录结构与模块组织](./directory-structure.md) | 多模块 starter 布局、模块清单、子包命名流派、版本机制 |
| [自动装配规约](./autoconfiguration-guidelines.md) | **核心模式**：`AutoConfiguration + Properties + .imports` 三件套、条件注解、Template 封装 |
| [错误处理与统一响应](./error-handling.md) | `R<T>` 响应体、`ResultCode`、`BusinessException`、两层全局异常处理、JSR-303 校验 |
| [日志规约](./logging-guidelines.md) | `@Slf4j` 约定、`{}` 占位符、级别语义 |
| [代码质量与规范](./quality-guidelines.md) | spring-javaformat（Tab）、PMD/P3C、Lombok/工具类惯例、版本与构建、测试、提交风格 |

---

## 快速定位

- **新增一个 starter 模块** → 先读[自动装配规约](./autoconfiguration-guidelines.md)与[目录结构](./directory-structure.md)。
- **写接口/抛业务异常** → [错误处理](./error-handling.md)。
- **提交前** → [代码质量](./quality-guidelines.md)的检查清单（`mvn spring-javaformat:apply` + 无新增 PMD 违规）。

---

## 维护

这些文档描述项目**当前实际**约定（含尚未统一的历史流派与待规整点，已在文中标注 ⚠️）。发现新约定或纠正偏差时，更新对应文档并保持本索引与文件集一致。
