# OpenAPI / Swagger UI

本项目使用 **[SpringDoc OpenAPI](https://springdoc.org/)**（适配 Spring Boot 4：根 `pom.xml` 中 **`springdoc.version`**）。

## 浏览器访问（local）

| 入口 | URL | 说明 |
|------|-----|------|
| 网关聚合 Swagger（下拉切换服务） | <http://localhost:18080/swagger-ui.html> | 定义来自 `/auth|/user|/order` 前缀转发的 **`/v3/api-docs`**；需网关与下游均已启动且注册到同一 Nacos。 |
| 认证中心文档 | <http://localhost:19082/swagger-ui.html> | 登录、`/oauth2/token` · OpenAPI：`/v3/api-docs` |
| 用户服务文档 | <http://localhost:19080/swagger-ui.html> | 需在页面 **Authorize** 填 Bearer：`store-auth-service` 签发的 `access_token` |
| 订单服务文档 | <http://localhost:19081/swagger-ui.html> | 同上 Bearer |

## 离线导出 JSON（CI / 存档）

在项目根目录、各服务已启动时执行：

```powershell
powershell -ExecutionPolicy Bypass -File ./scripts/export-openapi.ps1
```

生成文件：`docs/openapi/` 目录下 **`store-*-openapi.json`**。

生产环境（`SPRING_PROFILES_ACTIVE=prod`）已在各 **`application.yml`** 中 **`springdoc.*.enabled=false`**，默认不下发文档以避免暴露端口信息。
