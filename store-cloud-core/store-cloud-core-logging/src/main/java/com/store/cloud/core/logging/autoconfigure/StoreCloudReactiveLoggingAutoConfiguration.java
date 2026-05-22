package com.store.cloud.core.logging.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.store.cloud.core.logging.reactive.TraceIdReactiveWebFilter;

import org.springframework.web.server.WebFilter;

/**
 * WebFlux 环境（如网关）注册链路过滤器；不与 Servlet {@link StoreCloudLoggingAutoConfiguration} 共存。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFilter.class)
@EnableConfigurationProperties(StoreCloudLoggingProperties.class)
@ConditionalOnProperty(prefix = "store.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StoreCloudReactiveLoggingAutoConfiguration {

    @Bean
    TraceIdReactiveWebFilter storeCloudReactiveTraceWebFilter(StoreCloudLoggingProperties properties) {
        return new TraceIdReactiveWebFilter(properties);
    }
}
