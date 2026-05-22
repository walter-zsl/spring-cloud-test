# store-cloud-core-logging

**门面**：SLF4J。  
**实现**：不强制绑定；随业务应用的 `spring-boot-starter-*` 使用 Boot 默认 **Logback** 即可。

## 结构化 JSON（集中式日志）

classpath 上出现本模块时，`StoreCloudLoggingEnvironmentDefaultsPostProcessor`（在 **`META-INF/spring.factories`** 中注册为 `org.springframework.boot.EnvironmentPostProcessor`）会在**未手写**等价配置的前提下，注入便于 **ELK / Loki / Grafana** 采集的默认值：

| 生效条件 | 行为 |
|---------|------|
| `store.logging.apply-default-logging-patterns=true`（默认）且 `store.logging.structured-console=true`（默认） | `logging.structured.format.console=logstash`、`logging.structured.json.context.include=true`，控制台输出 **JSON 行**，并把 **SLF4J MDC（含 traceId）** 纳入上下文字段 |
| `store.logging.structured-console=false` | 退回团队 **文本**模板（仍含 **`%X{traceId}`**） |

可自行覆盖：`logging.structured.format.console`（例如 `ecs`）、`logging.structured.ecs.service.*`、`logging.pattern.console` 等；与 Spring Boot **4.x** 官方 **`logging.structured.*`** 一致，**无需**再引 `logstash-logback-encoder`。

## 与 `store-cloud-core-web` 的关系

本模块已作为 **`store-cloud-core-web` 的传递依赖**。业务 Servlet 微服务若已依赖 **web**，一般**无需**在业务 `pom.xml` 里再写 `store-cloud-core-logging`。  
**网关**：`store-cloud-gateway` **显式依赖**本模块（WebFlux 无 **web**，需单独引用），以获得 JSON 控制台默认与链路过滤器。

## 能力

| 内容 | 说明 |
|------|------|
| `MdcFieldNames` / `TraceMdc` | MDC 键名约定；任务链手写设置 traceId 时的辅助 |
| `TraceIdServletFilter` | **Servlet**：读/生成链路号 → **MDC**，响应头回传，`FilterOrder` 最高优先级 |
| `TraceIdReactiveWebFilter` | **WebFlux**（网关）：同上，并把 **`X-Trace-Id`** 写回转发请求头，下游 Servlet 服务可接续同一号 |

自动配置见 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`：`StoreCloudLoggingAutoConfiguration`（SERVLET）、`StoreCloudReactiveLoggingAutoConfiguration`（REACTIVE）。

## 分布式链路（X-Trace-Id）

1. **入站**：网关 / 上游可带 `X-Trace-Id`（名称可配）；未带则由过滤器生成 **32** 位十六进制串（无连字符 UUID）。  
2. **日志**：结构化 JSON 中通过 **`logging.structured.json.context.include=true`** 带出 MDC **`traceId`**（键名默认 `traceId`，可配 **`mdc-trace-id-key`**）。  
3. **响应**：默认在响应头回传同一追踪号；跨域时需 `Access-Control-Expose-Headers` 暴露该头名。

## 业务模块中引用

不经过 **web** 时显式依赖：

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
    apply-default-logging-patterns: true   # 关闭则本模块不写任何默认值
    structured-console: true               # false = 单行文本模板
    structured-console-format: logstash    # ecs / gelf 等见 Boot 文档
    enabled: true
    trace-header: X-Trace-Id
    mdc-trace-id-key: traceId              # MDC / JSON context 对齐
    expose-trace-id-response: true
    response-trace-header: ""               # 空则与 trace-header 相同
```

仅当 **关闭** 结构化控制台、需要自定义文本行时示例：

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [trace=%X{traceId:-}] - %msg%n"
```
