# store-cloud

企业级 Spring Cloud Alibaba 多仓库布局：**网关 + 业务契约（`store-cloud-service-api`）+ 可运行服务（`store-cloud-service`）**，公共能力在 **`store-cloud-core`**。BladeX 式 **`*-service` / `*-service-api` 分工**说明见 **`store-cloud-service-api/README.md`** 与 **`store-cloud-service/README.md`**。

## 仓库结构

```
store-cloud/
├── pom.xml
├── store-cloud-core/                # auth + response（契约） + logging（MDC/trace） + web（错误与安全壳）
├── store-cloud-gateway/
├── store-cloud-service-api/         # 业务域契约聚合（Feign + DTO）
│   ├── store-cloud-user-api/        # 用户契约
│   └── store-cloud-order-api/       # 订单契约
└── store-cloud-service/             # 可运行 Boot 应用聚合
    ├── store-cloud-auth/            # 认证中心（OAuth2/JWT 令牌派发）
    ├── store-cloud-user/            # 用户领域服务
    └── store-cloud-order/           # 订单服务
```

### 包结构（各业务服务，`com.store.cloud.{auth|user|order}` 下）

| 包 | 说明 |
|----|------|
| `config` | Spring `@Configuration`（Security 等） |
| `controller` | REST 控制器 |
| `service` / `service.impl` | 业务接口与实现 |
| `mapper` | MyBatis Mapper（占位，待接持久化） |
| `repository` / `repository.impl` | 仓储接口与自定义实现 |
| `model` | 领域模型 |
| `entity` | 与表对应的实体 |
| `dto` | 入参、层间传输对象（跨服务对外契约优先放 `*-api`） |
| `vo` | 返回视图对象（与 Feign 一致的模型可继续放 `*-api`） |
| `util` | 工具类 |

启动类仍放在包根目录，例如：`com.store.cloud.auth` / `com.store.cloud.user` / `com.store.cloud.order`。

## 服务与端口

| 服务 | `spring.application.name` | 默认端口 | 说明 |
|------|---------------------------|----------|------|
| Gateway | `store-gateway` | 18080 | 对外统一入口 |
| 认证中心 | `store-auth-service` | 19082 | JSON `/api/auth/login`、表单 OAuth2 **`POST /oauth2/token`**（`grant_type=password`）；对称 JWT **`iss`** 为 **`store-auth-service`** |
| 用户服务 | `store-user-service` | 19080 | 用户信息等（JWT Resource Server，`issue-tokens: false`） |
| 订单服务 | `store-order-service` | 19081 | 需携带与认证中心同一 **`JWT_SECRET`** 签发的 Bearer Token |

### 经网关访问（StripPrefix）

| 对外 URL 示例 | 转发后命中服务内路径 |
|---------------|----------------------|
| `POST http://localhost:18080/auth/api/auth/login` | **认证中心** `/api/auth/login`（Body：`{"username":"demo","password":"demo"}`） |
| `POST http://localhost:18080/auth/oauth2/token`（`Content-Type: application/x-www-form-urlencoded`，body：`grant_type=password&username=demo&password=demo`） | **认证中心** `/oauth2/token` |
| `GET http://localhost:18080/user/api/public/ping` | 用户服务 `/api/public/ping` |
| `GET http://localhost:18080/order/api/v1/orders` + `Authorization: Bearer ...` | 订单服务 `/api/v1/orders` |

## 核心库 `store-cloud-core`

见 **`store-cloud-core/README.md`**。业务服务按需依赖 **`store-cloud-core-auth`**（JWT、Servlet 安全底座）、**`store-cloud-core-web`**（统一错误 JSON + **`@ControllerAdvice`**，并传递 **`store-cloud-core-response`** 成功外层 `ApiEnvelope` 等）、可选 **`store-cloud-core-logging`**（SLF4J/MDC、请求 **traceId** 过滤器）。若某模块仅需 **契约类型**（如纯 Feign/API 且无 Spring MVC），可**只依赖** **`store-cloud-core-response`**。

- **`store-cloud-auth`**：左侧仅「用户名口令」链路；**`issue-tokens: true`**（签发）、**`validate-incoming-jwt: false`**（不校验入站 Bearer，避免双线 `SecurityFilterChain`）。
- **`store-cloud-user`**、**`store-cloud-order`**：**`issue-tokens: false`**、**`validate-incoming-jwt: true`**，OAuth2 Resource Server + 对称 **`JwtDecoder`**。

## 本地启动

1. 启动 **Nacos**（默认 `127.0.0.1:8848`）。  
2. VS Code：**Run** 中启动 **`store-cloud-auth`、`store-cloud-user`、`store-cloud-order`、`store-cloud-gateway`**（复合调试：**「网关 + auth + user + order (并行)」**）。  
   - 各服务 **`application.yml`** 已默认 **`spring.profiles.default=local`**；命令行手写仍请 **`SPRING_PROFILES_ACTIVE=local`**（或强密钥，见下「排障」）。  
   - 调试会先执行一次 **「Maven: compile workspace (full reactor)」**。  
3. **取 Token**：`POST http://localhost:19082/api/auth/login`（或网关 `POST .../auth/api/auth/login`），Body：`{"username":"demo","password":"demo"}`；响应字段为 **`access_token`**、**`token_type`**、**`expires_in`**（OAuth 2 snake_case）。  
4. 调订单：`GET http://localhost:19081/api/v1/orders`，Header：`Authorization: Bearer <access_token>`。  
5. 或经网关：`http://localhost:18080/order/api/v1/orders` 同上 Header。

### IDE：`Maven Dependencies` 指向不存在的 `store-cloud-core-logging-*.jar`（如 Eclipse 964）

**原因**：`store-cloud-order` → **`store-cloud-core-web`** → 传递 **`com.store:store-cloud-core-logging`**。若本地 **`~/.m2`** 中从未安装过该 SNAPSHOT Jar（例如新开仓库或只打开了子模块而从未在根 reactor 编译/安装），IDE 仍会生成指向该路径的 Classpath，从而产生「文件不存在」类错误。

**处理**：

1. 在仓库根目录 **`store-cloud/`** 执行一次安装（任选其一）：  
   - 仅链路所需：**`mvn install -pl store-cloud-service/store-cloud-order -am -DskipTests`**  
   - 或全量：**`mvn install -DskipTests`**
2. Eclipse：**右键对应工程 → Maven → Update Project**（必要时勾选 **Force Update of Snapshots/Releases**）。更稳妥的做法是以根 **`store-cloud/pom.xml`** 导入多模块 Maven 工程；若 Maven 勾选 **Resolve dependencies from Workspace projects**，在已导入 **`store-cloud-core-logging`** 子工程时 Classpath 可走工作区而不仅依赖 **`~/.m2`**。
3. 若仍报错，删除本地目录 **`~/.m2/repository/com/store/store-cloud-core-logging/`** 后重跑一次上述 **`mvn install`**（少见：损坏的 SNAPSHOT 元数据）。

### 排障（用户 / 订单起不来而网关正常）

| 现象 | 常见原因 |
|------|----------|
| 启动报错 **禁止使用内置占位密钥**、`IllegalArgumentException`（Jwt） | **`local`** 未生效或未配置 **`JWT_SECRET`**：本地请在 **`application-local.yml`**（或 **`spring.profiles.default=local`**）中关闭占位密钥强校验，或改用强密钥（UTF-8 ≥32 字节且不含内置占位片段）；**认证中心、用户服务、订单服务**须使用同一 **`JWT_SECRET`**。**网关为 WebFlux，不装载 Servlet JWT 套件**，有时会表现为只有网关可用。 |
| **`store-cloud-auth` 卡住 / 多端 Security 冲突** | 认证中心 YAML 中确认 **`validate-incoming-jwt: false`**；否则 classpath 上的 **`oauth2-resource-server`** + **`JwtDecoder` Bean** 会触发另一条默认资源服务器链，与手写表单登录链共存时常见启动失败。 |
| **`preLaunchTask` / Maven** 报错 `Unrecognized option`… | JDK 与 **`.mvn/jvm.config`** 不兼容；可将该文件**清空**，或改写为当前 JDK 支持的参数。 |

| 场景 | 做法 |
|------|------|
| **本地** | `SPRING_PROFILES_ACTIVE=local`；认证/用户/订单模块可用 **`application-local.yml`** 覆盖 |
| **生产** | `SPRING_PROFILES_ACTIVE=prod`，由 Apollo 注入密钥与业务配置；详见各服务 `application.yml` 中 `optional:apollo://application` |

## Maven + 较新 JDK

根 **`pom.xml`** 中 **`java.version` 当前为 `25`**，命令行执行 **`mvn install` / `compile`** 时 **`JAVA_HOME` / Maven 所用的 JVM 也需为 JDK 25**；否则会报「不支持发行版本 25」等编译失败，严重时 IDE/M2E 的依赖解析 Classpath 也会出现「构建路径错误」。本地可与 **`.vscode/settings.json`** 中的 **`java.jdt.ls.java.home`** 对齐为同一 JDK 安装路径。

可选：根目录 **`.mvn/jvm.config`** 为 Maven 进程附加 JVM 参数（若报错 `Unrecognized option` 可先**清空该文件**）。全量编译：

```bash
mvn -q -pl store-cloud-service-api,store-cloud-service -am compile -DskipTests
```

## 组件扫描与统一错误响应

| 要点 | 说明 |
|------|------|
| **扫包** | 启动类仅用 `scanBasePackages = com.store.cloud.{auth\|user\|order}`，**不扫描**整块 `com.store.cloud`。 |
| **core 装配** | `store-cloud-core-auth`、**`store-cloud-core-logging`** 与 **`store-cloud-core-web`** 各有 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。**`store-cloud-core-response`** 仅为 Jar 契约（无自动配置条目）。网关（WebFlux）不引用 **auth/web/logging**。 |
| **拆分** | **`store-cloud-core-response`**：**`ApiEnvelope`** 等成功外层（**`com.store.cloud.core.response.api`**）；**`store-cloud-core-web`**：**`ApiErrorResponse` / `@ControllerAdvice`**（**`com.store.cloud.core.web.error`**）；二者均为 Maven 构件，非独立部署的微服务进程。Servlet 应用通常依赖 **`web`**（即同时带上 **response**）。 |
| **错误 JSON** | 见 **web**：`ApiErrorResponse`、`ErrorCodes`、`BusinessException`、`GlobalRestExceptionAdvice`。**FilterSecurity** 链路 401/403 仍可后续配置 `AuthenticationEntryPoint` **等同形态 JSON**。 |
| **成功包装** | 见 **response**：`ApiEnvelope`、`PagedPayload`、`PageMeta`。旧式 **`ApiEnvelope.failed`** 仍可兼容；新项目出错建议 **`ApiErrorResponse` + HTTP 状态码**。 |

## OpenAPI / Swagger UI

参见 **`docs/swagger.md`**（各端口 Swagger UI、网关聚合、`scripts/export-openapi.ps1` 导出 JSON）。
