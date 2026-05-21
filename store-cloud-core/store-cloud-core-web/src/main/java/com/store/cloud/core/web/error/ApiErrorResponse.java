package com.store.cloud.core.web.error;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * REST 出错时的统一外层 JSON（camelCase）。
 * @param traceId 与 {@code store.logging.mdcTraceIdKey}（默认 {@code traceId}）及响应头 {@code X-Trace-Id} 一致；无链路上下文时为 {@code null}，序列化时省略。
 * @param fields 表单/参数校验时为字段→说明；其它错误通常为 {@code null}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String path,
        String code,
        String message,
        OffsetDateTime timestamp,
        String traceId,
        Map<String, String> fields) {

    public static ApiErrorResponse of(ErrorCodes canonical, String requestPath, String message, String traceId) {
        return new ApiErrorResponse(
                requestPath,
                canonical.businessCode(),
                message != null ? message : canonical.businessCode(),
                OffsetDateTime.now(),
                traceId,
                null);
    }

    public static ApiErrorResponse of(
            String businessCode,
            String requestPath,
            String message,
            Map<String, String> fieldErrors,
            String traceId) {
        Map<String, String> copy =
                fieldErrors == null ? null : Collections.unmodifiableMap(fieldErrors);
        String msg =
                message != null
                        ? message
                        : (businessCode != null ? businessCode : ErrorCodes.STORE_HTTP_STATUS.businessCode());
        return new ApiErrorResponse(requestPath, businessCode, msg, OffsetDateTime.now(), traceId, copy);
    }
}
