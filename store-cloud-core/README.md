# store-cloud-core（核心目录）

Maven 聚合工程：**`artifactId === store-cloud-core`**，`packaging` 为 `pom`，只做子模块收口，源码放在子目录。

## 当前子模块

| 目录 | Maven 坐标 | 说明 |
|------|------------|------|
| **`store-cloud-core-auth/`** | `com.store:store-cloud-core-auth` | JWT（对称 HS256）编解码、Servlet 安全配置、JWT 委派签发器、OAuth2 snake_case 响应体等底座 |

## 追加新能力的步骤

1. 在 **`store-cloud-core/`** 下新建子目录，例如 **`store-cloud-core-xxx/`**，`artifactId` 建议使用同一前缀：`store-cloud-core-xxx`。
2. 新建 **`pom.xml`**，`<parent>` 指向 **`store-cloud-core`**（`relativePath` 指向 **`../pom.xml`**），`<artifactId>` 与目录名保持一致。
3. 在 **`store-cloud-core/pom.xml`** → **`<modules>`** 追加一行：**`<module>store-cloud-core-xxx</module>`**。

业务 Boot 模块按需 **`dependency`** 具体子构件（不必依赖整个 aggregator）。
