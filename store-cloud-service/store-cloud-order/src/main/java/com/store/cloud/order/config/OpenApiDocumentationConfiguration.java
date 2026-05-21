package com.store.cloud.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/** OpenAPI 3：订单 JWT Resource Server——试调受保护接口前在 Swagger 「Authorize」中填写 Bearer JWT。 */
@Configuration(proxyBeanMethods = false)
public class OpenApiDocumentationConfiguration {

    @Bean
    OpenAPI orderOpenApi(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.port}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " · 订单域")
                        .version("1.0.0-SNAPSHOT")
                        .description("除 `/api/public/**` 外需 Bearer Token (`store-auth-service` 签发，`JWT_SECRET` 一致)。"))
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
