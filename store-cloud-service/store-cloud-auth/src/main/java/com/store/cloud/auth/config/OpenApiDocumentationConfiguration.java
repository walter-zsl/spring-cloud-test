package com.store.cloud.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI 3 / Swagger UI 元数据（令牌本身无 Bearer：登录端点放行；「bearer-jwt」供文档侧说明给用户/订单等服务的调用）。
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiDocumentationConfiguration {

    @Bean
    OpenAPI authOpenApi(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.port}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " · 认证中心")
                        .version("1.0.0-SNAPSHOT")
                        .description(
                                "JSON 登录：`POST /api/auth/login`；OAuth 2：`POST /oauth2/token`（`grant_type=password`）。\n"
                                        + "签发对称 JWT，`iss` 见 `store.security.jwt.issuer`；与各业务 JVM 须使用相同的 `JWT_SECRET`。"))
                .addServersItem(
                        new Server().url("http://localhost:" + serverPort).description("直连 JVM"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("业务服务（store-user/order）需在 Header 中带：Authorization: Bearer <access_token>")));
    }
}
