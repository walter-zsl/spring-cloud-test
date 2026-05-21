# store-cloud-core-logging

**门面**：SLF4J。  
**实现**：不强制绑定；随业务应用的 `spring-boot-starter-*` 使用 Boot 默认 **Logback** 即可。

## 与 `store-cloud-core-web` 的关系（不必每个服务手写依赖）

本模块已作为 **`store-cloud-core-web` 的传递依赖**。业务微服务若已依赖 **web**（推荐：统一错误 + 成功契约），则会**自动**带上 **logging**，一般**无需**在业务 `pom.xml` 里再写一遍 `store-cloud-core-logging`。

仅在不依赖 web、但仍要 MDC 工具类时，才单独依赖本模块。

## 全局默认日志格式（可覆盖）

类路径上存在本模块时，若未配置 `logging.pattern.console`，会通过
`StoreCloudLoggingEnvironmentDefaultsPostProcessor` 注入团队统一模板（含 **`%X{traceId}`**）。
在 `application.yml` 里写上自己的 `logging.pattern.console` 即可完全覆盖。

关闭默认注入（例如由 Apollo 统一下发 pattern）：

```yaml
store:
  logging:
    apply-default-logging-patterns: false
```

## 能力

| 内容 | 说明 |
|------|------|
| `MdcFieldNames` / `TraceMdc` | MDC 键名约定与手写任务链中设置 traceId 的辅助方法 |
| `TraceIdServletFilter` | （Servlet）从请求头读取或生成 traceId，**写入 MDC**、**响应头回传**，请求结束清理 MDC |

自动配置通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册；**仅 Servlet Web** 环境生效，网关（WebFlux）请勿依赖本模块。

## 分布式链路（X-Trace-Id）

1. **入站**：网关 / 上游调用可带 `X-Trace-Id`（名称可配）；未带则服务生成 32 位十六进制串（无连字符 UUID）。  
2. **日志**：同一请求内日志通过 MDC 带出 `traceId`（键名可配，与 `%X{traceId}` 对齐）。  
3. **出站 HTTP**：默认在**响应头**写回同一追踪号（默认头名与入站一致：`X-Trace-Id`），便于浏览器/客户端、下游 Feign 继续**透传**该头，与 ELK 中日志关联。

跨域场景若需在前端 JS 读取响应头，请在网关或服务上配置 `Access-Control-Expose-Headers: X-Trace-Id`（或你自定义的响应头名）。

## 业务模块中引用

仅当**不**使用 `store-cloud-core-web` 时需显式添加：

```xml
<dependency>
    <groupId>com.store</groupId>
    <artifactId>store-cloud-core-logging</artifactId>
    <version>${project.version}</version>
</dependency>
```

## 可选配置（`application.yml`）

```yaml
store:
  logging:
    enabled: true
    trace-header: X-Trace-Id       # 入站请求头
    mdc-trace-id-key: traceId    # MDC 键，与 %X{traceId} 一致
    expose-trace-id-response: true   # 是否在响应头回传（默认 true）
    response-trace-header: ""      # 响应头名；留空则与 trace-header 相同
```

与 Spring Boot 单行日志格式（在业务 `application.yml` 中）：

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [trace=%X{traceId:-}] - %msg%n"
```

## 集中式 JSON 日志（可选）

需要对接 ELK / Loki 时，在**业务应用**中额外添加（版本随仓库统一 BOM 或自行对齐 Logback）：

`net.logstash.logback:logstash-logback-encoder`

再在 Logback 中配置 `LoggingEventCompositeJsonEncoder` 等；本核心模块不重复引入以免与现场版本冲突。
