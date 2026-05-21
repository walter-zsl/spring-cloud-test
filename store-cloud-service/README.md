# store-cloud-service（可运行业务服务目录）

Maven 聚合模块 **`store-cloud-service`**（`packaging=pom`），收录可运行的 Boot 应用。各域契约见 **`store-cloud-service-api/`**。

## 当前子模块

| 目录 | Maven 坐标（节选） | 说明 |
|------|-------------------|------|
| **`store-cloud-auth/`** | `store-cloud-auth` | **认证中心**：OAuth 2 `/oauth2/token`（`password`）、JSON `/api/auth/login`，JWT 派发（共用 **`store-cloud-core-auth`**） |
| **`store-cloud-user/`** | `store-cloud-user` | **用户领域**（Resource Server：`/api/v1/users/me`） |
| **`store-cloud-order/`** | `store-cloud-order` | **订单**：JWT Resource Server，`order-api` DTO |

各子模块源码目录约定见根目录 **`README.md`** 中「包结构」表（`config` / `controller` / `service` / `mapper` / `repository` / `model` / `entity` / `dto` / `vo` / `util`）。

## 与 BladeX 中 `xxx-service` / `xxx-service-api` 的对应关系

| BladeX 常见模块 | 本质 | 本项目对应 |
|-----------------|------|------------|
| **`blade-xx-service`** | 可部署 Boot 应用 | **`store-cloud-auth`**、**`store-cloud-user`**、**`store-cloud-order`**（本目录） |
| **`blade-xx-service-api`** | 契约 jar | **`store-cloud-service-api`** 下的 **`store-cloud-user-api`**、**`store-cloud-order-api`** |
