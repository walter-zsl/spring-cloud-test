# store-cloud-service-api（业务域契约聚合）

Maven 聚合模块 **`store-cloud-service-api`**（`packaging=pom`），收录各域 **`*-api` 子模块**：**Feign 接口与 DTO jar**，无启动类，供网关或其它服务依赖。

## 当前子模块

| 目录 | Maven 坐标（节选） | 说明 |
|------|-------------------|------|
| **`store-cloud-user-api/`** | `store-cloud-user-api` | 用户域 **Feign + DTO** |
| **`store-cloud-order-api/`** | `store-cloud-order-api` | 订单域 **Feign + DTO** |

可运行 Boot 应用见同级 **`store-cloud-service/`**。

## 与 BladeX 的对应关系

| BladeX 常见模块 | 本质 | 本项目对应 |
|-----------------|------|------------|
| **`blade-xx-service-api`** | 契约 jar：Feign、DTO | **`store-cloud-user-api`**、**`store-cloud-order-api`**（本目录下） |
| **`blade-xx-service`** | 可部署 Boot | **`store-cloud-user`**、**`store-cloud-order`**（`store-cloud-service/`） |

**`*-api`** 只声明「如何调用、数据结构是什么」；**`*-service`** 实现 Controller 并与契约 DTO **对齐**。
