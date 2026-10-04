# Spring Boot 3.5.16 配套依赖升级

日期：2026-10-02。基线：Java 17、Spring Boot 3.5.16；Maven 在本机使用 JDK 21，以 `--release 17` 编译。

## 目标与版本边界

更新兼容版本线中的配套依赖，修复 Commons Lang 已知漏洞，验证组件编译及重点运行路径。保持 Boot 3、Redisson 3、OSHI 6、PMD 6；OkHttp 4.12.0、p6spy starter 1.12.1 保留。

- Spring Cloud：2025.0.3；MyBatis-Plus：3.5.17。
- springdoc：2.9.1；Swagger Jakarta：2.2.55。
- Lombok：1.18.46；Authorization Server：1.5.8，均继承 Boot BOM。
- Commons Lang：3.21.0；Codec：1.22.1；IO：2.22.0；Collections：4.6.0。
- Guava：33.7.2-jre；jsoup：1.23.2；AWS SDK：2.55.10。
- Redisson：3.52.0；OSHI：6.12.0，移至根 POM 统一管理。
- Compiler：3.16.0；Surefire：3.6.0；Jar：3.5.1；Resources：3.5.0。
- Flatten：1.8.0；Build Helper：3.6.2；Central Publishing：0.11.0；Java Format：0.0.48。
- Maven PMD Plugin：3.21.2；P3C：2.2.1.0x，仍使用 PMD 6.55.0。

来源：[Boot BOM](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/3.5.16/spring-boot-dependencies-3.5.16.pom)、[Cloud 兼容矩阵](https://spring.io/projects/spring-cloud/)、[springdoc 2.x](https://springdoc.org/v2/)、[Commons Lang 安全公告](https://lists.apache.org/thread/bgv0lpswokgol11tloxnjfzdl7yrc1g1)。其他版本来自各制品的 Maven Central 元数据。

## 兼容修复

- Redisson 新增异步 `subscribeOnElements` 重载后，反射调用的表达式 Lambda 同时匹配两个签名。延迟队列监听器改用无返回值的代码块 Lambda，保留同步 Consumer 语义。
- Stream 集成测试发现 `RuntimeUtil.getPId()` 原有 `Integer.parseInt(text, -1)` 错误，第二个参数实际是进制。改为十进制解析；新增测试先复现失败，再验证结果等于当前进程编号。
- Toolkit 的 Swagger 注解依赖切换为 Jakarta 制品，与 springdoc 使用相同坐标，避免重复类。

## 验证方式

全模块构建：`mvn -B -ntp clean verify`。按仓库默认值跳过测试，包含 Java Format 和 PMD；PMD 原有 `failOnViolation=false` 配置不变。

独立运行的测试：

```bash
mvn -B -ntp test -pl libre-toolkit,libre-oss -am \
  -Dmaven.test.skip=false -DskipTests=false
```

Redis 集成测试必须使用独立的本机 Redis 测试实例。先启动不持久化的实例，再在另一终端执行测试；结束后停止该测试实例：

```bash
redis-server --bind 127.0.0.1 --port 16389 --save "" --appendonly no

mvn -B -ntp test -pl libre-redisson -am \
  -Dmaven.test.skip=false -DskipTests=false \
  -Dtest=RedissonCompatibilityIT -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest.redis.address=redis://127.0.0.1:16389
```

`RedissonCompatibilityIT` 不属于 Surefire 默认匹配的类名，需显式选择；未传测试地址时直接失败。测试使用随机前缀隔离数据，覆盖默认 Codec 读写、数字命令、锁获取释放、延迟队列回调和 Stream 回调。本次运行通过临时端口启动独立 Redis，测试后已终止实例。

## 已验证结果与限制

- 最终 `mvn -B -ntp clean verify` 全部 15 个模块构建成功；Java Format 验证和 `git diff --check` 通过。
- 定向测试 8 项通过：工具模块 3 项、Redisson 4 项、OSS 1 项；无失败、无跳过。
- OSS 验证客户端初始化、TransferManager 创建及 GET/PUT 预签名；尚未验证真实对象存储的上传、下载与 multipart。
- 依赖树确认 Authorization Server 1.5.8、Lombok 1.18.46、Commons Lang 3.21.0、Redisson 3.52.0，Swagger 3 仅使用 Jakarta 2.2.55。
- 生成的对外 BOM 已核验 16 项关键依赖版本，包含 AWS SDK、Redisson、OSHI 与 Swagger；未残留 `${revision}` 占位符。
- 发布配置的 `mvn -B -ntp -N -Prelease validate` 通过；未签名或发布制品。
- Redisson 对 `RDelayedQueue` 输出弃用日志；真实投递测试通过，队列替换涉及公开 API 和行为迁移，本次未替换。
- PMD 仍报告存量规范问题，`libre-ip2region` 仍出现 `aktStatus is NULL: maximum Iterations exceeded` 分析器错误，静态分析不能视为完全通过。
- Redis 集群、Sentinel、故障转移及 RabbitMQ/MQTT 外部服务测试未运行。
