package com.store.cloud.core.logging.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import com.store.cloud.core.logging.web.TraceIdServletFilter;

import jakarta.servlet.DispatcherType;

/**
 * Servlet 环境下注册 {@link TraceIdServletFilter}；实现侧沿用 Spring Boot 默认 Logback Starter，无需在本模块再引 Logback 坐标。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(StoreCloudLoggingProperties.class)
@ConditionalOnProperty(prefix = "store.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StoreCloudLoggingAutoConfiguration {

    @Bean
    FilterRegistrationBean<TraceIdServletFilter> storeCloudTraceIdServletFilter(
            StoreCloudLoggingProperties properties) {
        FilterRegistrationBean<TraceIdServletFilter> reg = new FilterRegistrationBean<>(new TraceIdServletFilter(properties));
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.addUrlPatterns("/*");
        reg.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
        return reg;
    }
}
