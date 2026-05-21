package com.store.cloud.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI 3：JWT Resource Server——除公开接口外，需在 Swagger 右上角「Authorize」填入 Bearer Token（由认证中心签发）。
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiDocumentationConfiguration {

    @Bean
    OpenAPI userOpenApi(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.port}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " · 用户域")
                        .version("1.0.0-SNAPSHOT")
                        .description("业务接口需在 Header：`Authorization: Bearer <access_token>`（由 `store-auth-service` 获取）。"))
                .addServersItem(new Server().url("http://localhost:" + serverPort).description("直连 JVM"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Raw Token：`eyJhbGciOi...`")));
    }
}
