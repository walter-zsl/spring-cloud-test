# store-cloud

企业级 Spring Cloud Alibaba 多仓库布局：**网关 + 业务契约（`store-cloud-service-api`）+ 可运行服务（`store-cloud-service`）**，公共能力在 **`store-cloud-core`**。BladeX 式 **`*-service` / `*-service-api` 分工**说明见 **`store-cloud-service-api/README.md`** 与 **`store-cloud-service/README.md`**。

## 仓库结构

```
store-cloud/
├── pom.xml
├── store-cloud-core/                # 核心库聚合（如 store-cloud-core-auth）
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

见 **`store-cloud-core/README.md`**。业务服务按需依赖 **`store-cloud-core-auth`**（JWT、Servlet 安全底座）。

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

可选：根目录 **`.mvn/jvm.config`** 为 Maven 进程附加 JVM 参数（若报错 `Unrecognized option` 可先**清空该文件**）。全量编译：

```bash
mvn -q -pl store-cloud-service-api,store-cloud-service -am compile -DskipTests
```

## OpenAPI / Swagger UI

参见 **`docs/swagger.md`**（各端口 Swagger UI、网关聚合、`scripts/export-openapi.ps1` 导出 JSON）。
