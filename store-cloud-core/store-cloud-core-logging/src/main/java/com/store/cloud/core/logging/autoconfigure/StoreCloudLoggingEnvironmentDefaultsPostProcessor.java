package com.store.cloud.core.logging.autoconfigure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * 在未显式配置时注入团队统一的控制台日志 pattern（含 {@code %X{traceId}}），各服务可通过
 * {@code logging.pattern.console} 或关闭 {@code store.logging.apply-default-logging-patterns} 覆盖。
 */
public class StoreCloudLoggingEnvironmentDefaultsPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * ISO-8601 时间、级别、线程、traceId、logger、消息；与 {@link TraceIdServletFilter} 写入的 MDC 键 {@code traceId} 对齐。
     */
    public static final String DEFAULT_CONSOLE_PATTERN =
            "%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %5p [%15.15t] [%X{traceId:-}] %-40.40logger{39} : %m%n%wEx";

    static final String PROPERTY_APPLY_DEFAULTS = "store.logging.apply-default-logging-patterns";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getProperty(PROPERTY_APPLY_DEFAULTS, boolean.class, true)) {
            return;
        }
        Map<String, Object> defaults = new LinkedHashMap<>(1);
        if (environment.getProperty("logging.pattern.console") == null) {
            defaults.put("logging.pattern.console", DEFAULT_CONSOLE_PATTERN);
        }
        if (!defaults.isEmpty()) {
            environment
                    .getPropertySources()
                    .addLast(new MapPropertySource("store-cloud-logging-default-patterns", defaults));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
