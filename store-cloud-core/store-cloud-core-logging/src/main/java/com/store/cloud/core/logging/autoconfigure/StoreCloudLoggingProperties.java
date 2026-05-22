package com.store.cloud.core.logging.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "store.logging")
public class StoreCloudLoggingProperties {

    /**
     * 是否在未显式配置 {@code logging.structured.format.console} 时，由环境后处理器注入团队默认（JSON + MDC 上下文）。
     * 关闭后请自行配置 {@code logging.pattern.console} 或 {@code logging.structured.*}。
     */
    private boolean structuredConsole = true;

    /** 结构化控制台格式 id（默认 {@code logstash}，便于集中式采集）。 */
    private String structuredConsoleFormat = "logstash";

    /** 是否注册链路过滤器（Servlet / WebFlux），为每条请求写入 traceId 至 MDC。 */
    private boolean enabled = true;

    /** 优先从该 HTTP 头读取上游追踪号（无则自动生成）。 */
    private String traceHeader = "X-Trace-Id";

    /** 写入 MDC 时使用的键（默认与 {@link com.store.cloud.core.logging.MdcFieldNames#TRACE_ID} 一致）。 */
    private String mdcTraceIdKey = com.store.cloud.core.logging.MdcFieldNames.TRACE_ID;

    /**
     * 是否在 HTTP 响应头中回传当前追踪号（分布式链路：下游可把同号再打给更下游，并与日志对齐）。
     */
    private boolean exposeTraceIdResponse = true;

    /**
     * 响应头名；为空则与 {@link #traceHeader} 一致（通常均为 {@code X-Trace-Id}）。
     */
    private String responseTraceHeader = "";

    public boolean isStructuredConsole() {
        return structuredConsole;
    }

    public void setStructuredConsole(boolean structuredConsole) {
        this.structuredConsole = structuredConsole;
    }

    public String getStructuredConsoleFormat() {
        return structuredConsoleFormat;
    }

    public void setStructuredConsoleFormat(String structuredConsoleFormat) {
        this.structuredConsoleFormat = structuredConsoleFormat;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTraceHeader() {
        return traceHeader;
    }

    public void setTraceHeader(String traceHeader) {
        this.traceHeader = traceHeader;
    }

    public String getMdcTraceIdKey() {
        return mdcTraceIdKey;
    }

    public void setMdcTraceIdKey(String mdcTraceIdKey) {
        this.mdcTraceIdKey = mdcTraceIdKey;
    }

    public boolean isExposeTraceIdResponse() {
        return exposeTraceIdResponse;
    }

    public void setExposeTraceIdResponse(boolean exposeTraceIdResponse) {
        this.exposeTraceIdResponse = exposeTraceIdResponse;
    }

    public String getResponseTraceHeader() {
        return responseTraceHeader;
    }

    public void setResponseTraceHeader(String responseTraceHeader) {
        this.responseTraceHeader = responseTraceHeader;
    }

    /** 实际写入响应的头名：显式配置了 {@link #responseTraceHeader} 时用之，否则与入站 {@link #traceHeader} 相同。 */
    public String resolveResponseTraceHeaderName() {
        if (responseTraceHeader != null && !responseTraceHeader.isBlank()) {
            return responseTraceHeader.trim();
        }
        return traceHeader;
    }
}
