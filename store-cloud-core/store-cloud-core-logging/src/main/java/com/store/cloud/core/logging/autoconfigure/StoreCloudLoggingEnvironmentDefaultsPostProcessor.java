package com.store.cloud.core.logging.autoconfigure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * 在未显式配置时注入：
 * <ul>
 *     <li>控制台 <b>JSON 结构化</b>（Spring Boot {@code logging.structured.*}），含 MDC/上下文字段（链路 {@code traceId}）</li>
 *     <li>或对 human-readable：<b>pattern</b>（含 {@code %X{traceId}}）</li>
 * </ul>
 */
public class StoreCloudLoggingEnvironmentDefaultsPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * ISO-8601 时间、级别、线程、traceId、logger、消息；与 {@link com.store.cloud.core.logging.web.TraceIdServletFilter} /
     * {@link com.store.cloud.core.logging.reactive.TraceIdReactiveWebFilter} 写入的 MDC 键对齐。
     */
    public static final String DEFAULT_CONSOLE_PATTERN =
            "%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %5p [%15.15t] [%X{traceId:-}] %-40.40logger{39} : %m%n%wEx";

    static final String PROPERTY_APPLY_DEFAULTS = "store.logging.apply-default-logging-patterns";

    /** 默认开启控制台 JSON（对接 ELK/Loki 等）；关闭后回退为 {@link #DEFAULT_CONSOLE_PATTERN}。 */
    static final String PROPERTY_STRUCTURED_CONSOLE = "store.logging.structured-console";

    /** 格式 id：{@code logstash}、{@code ecs}、{@code gelf} 或自定义 formatter 全类名。 */
    static final String PROPERTY_STRUCTURED_FORMAT = "store.logging.structured-console-format";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getProperty(PROPERTY_APPLY_DEFAULTS, boolean.class, true)) {
            return;
        }
        Map<String, Object> defaults = new LinkedHashMap<>(4);
        boolean structuredConsole = environment.getProperty(PROPERTY_STRUCTURED_CONSOLE, boolean.class, true);

        if (structuredConsole) {
            if (environment.getProperty("logging.structured.format.console") == null) {
                String fmt = environment.getProperty(PROPERTY_STRUCTURED_FORMAT, "logstash");
                defaults.put("logging.structured.format.console", fmt != null ? fmt.trim() : "logstash");
            }
            if (environment.getProperty("logging.structured.json.context.include") == null) {
                defaults.put("logging.structured.json.context.include", true);
            }
        } else if (environment.getProperty("logging.pattern.console") == null) {
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
