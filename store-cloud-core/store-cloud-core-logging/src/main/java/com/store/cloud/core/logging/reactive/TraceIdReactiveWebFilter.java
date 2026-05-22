package com.store.cloud.core.logging.reactive;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.store.cloud.core.logging.autoconfigure.StoreCloudLoggingProperties;

import reactor.core.publisher.Mono;

/**
 * WebFlux/Reactor-Netty（如网关）：与 {@link com.store.cloud.core.logging.web.TraceIdServletFilter} 行为对齐，
 * 读或生成链路号、写入 MDC、回写响应头，并在发往下一跳（含网关转发）的请求上补足 {@link StoreCloudLoggingProperties#getTraceHeader()}。
 */
public final class TraceIdReactiveWebFilter implements WebFilter, Ordered {

    private final StoreCloudLoggingProperties properties;

    public TraceIdReactiveWebFilter(StoreCloudLoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }
        String incoming = exchange.getRequest().getHeaders().getFirst(properties.getTraceHeader());
        String traceId =
                incoming != null && !incoming.isBlank()
                        ? incoming.trim()
                        : UUID.randomUUID().toString().replace("-", "");

        String mdcKey = properties.getMdcTraceIdKey();
        ServerHttpRequest mutatedRequest =
                exchange.getRequest().mutate().header(properties.getTraceHeader(), traceId).build();
        ServerWebExchange mutated = exchange.mutate().request(mutatedRequest).build();

        if (properties.isExposeTraceIdResponse()) {
            mutated.getResponse().getHeaders().set(properties.resolveResponseTraceHeaderName(), traceId);
        }

        MDC.put(mdcKey, traceId);
        return chain.filter(mutated).doFinally(s -> MDC.remove(mdcKey));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
