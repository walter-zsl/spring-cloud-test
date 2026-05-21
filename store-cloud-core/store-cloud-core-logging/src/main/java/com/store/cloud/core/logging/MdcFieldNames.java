package com.store.cloud.core.logging;

/**
 * SLF4J {@link org.slf4j.MDC} 键名约定，便于 Logback Pattern / JSON Encoder 统一引用。
 */
public final class MdcFieldNames {

    /** 分布式追踪标识（请求级；也可由上游网关 / Sidecar 注入）。 */
    public static final String TRACE_ID = "traceId";

    private MdcFieldNames() {}
}
