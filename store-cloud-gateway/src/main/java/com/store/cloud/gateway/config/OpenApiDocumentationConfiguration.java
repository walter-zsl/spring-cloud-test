package com.store.cloud.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * 网关层 OpenAPI/Swagger UI：<b>聚合</b>各下游的 {@code /v3/api-docs}（需在 {@code application.yml}
 * {@code springdoc.swagger-ui.urls} 中列出）。本 Bean 仅代表网关文档壳，详细路径仍来自各 Boot 实例。
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiDocumentationConfiguration {

    @Bean
    OpenAPI gatewayOpenApi(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.port}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " · 聚合文档入口")
                        .version("1.0.0-SNAPSHOT")
                        .description("通过路由前缀聚合 Auth / User / Order 的 OpenAPI。浏览器访问：`/swagger-ui.html`，"
                                + "并在顶部下拉中选择服务定义。"))
                .addServersItem(new Server().url("http://localhost:" + serverPort).description("经网关的根地址"));
    }
}
