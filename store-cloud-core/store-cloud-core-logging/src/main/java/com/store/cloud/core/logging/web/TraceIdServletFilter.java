package com.store.cloud.core.logging.web;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import com.store.cloud.core.logging.autoconfigure.StoreCloudLoggingProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 为每个 Servlet 请求写入追踪标识至 MDC，并在响应头回传（默认同名 {@code X-Trace-Id}），便于分布式链路与日志对齐。
 */
public class TraceIdServletFilter extends OncePerRequestFilter {

    private final StoreCloudLoggingProperties properties;

    public TraceIdServletFilter(StoreCloudLoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String incoming = request.getHeader(properties.getTraceHeader());
        String traceId =
                (incoming == null || incoming.isBlank())
                        ? UUID.randomUUID().toString().replace("-", "")
                        : incoming.trim();
        String key = properties.getMdcTraceIdKey();
        MDC.put(key, traceId);
        if (properties.isExposeTraceIdResponse()) {
            response.setHeader(properties.resolveResponseTraceHeaderName(), traceId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(key);
        }
    }
}
