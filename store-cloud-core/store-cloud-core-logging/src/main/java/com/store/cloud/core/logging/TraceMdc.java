package com.store.cloud.core.logging;

import java.util.UUID;

import org.slf4j.MDC;

/**
 * 基于 MDC 的追踪上下文辅助类（与具体日志实现无关，默认栈为 Logback）。
 */
public final class TraceMdc {

    private TraceMdc() {}

    /** 将 {@link MdcFieldNames#TRACE_ID} 写入 MDC；{@code traceId} 为空时生成 UUID。 */
    public static String putTraceId(String traceId) {
        String id = (traceId == null || traceId.isBlank()) ? UUID.randomUUID().toString().replace("-", "") : traceId;
        MDC.put(MdcFieldNames.TRACE_ID, id);
        return id;
    }

    public static void clearTraceId() {
        MDC.remove(MdcFieldNames.TRACE_ID);
    }
}
