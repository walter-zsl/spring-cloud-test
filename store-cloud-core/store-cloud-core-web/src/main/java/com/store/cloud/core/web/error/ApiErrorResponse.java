package com.store.cloud.core.web.error;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * REST 出错时的统一外层 JSON（camelCase）。
 * @param fields 表单/参数校验时为字段→说明；其它错误通常为 {@code null}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String path,
        String code,
        String message,
        OffsetDateTime timestamp,
        Map<String, String> fields) {

    public static ApiErrorResponse of(ErrorCodes canonical, String requestPath, String message) {
        return new ApiErrorResponse(
                requestPath,
                canonical.businessCode(),
                message != null ? message : canonical.businessCode(),
                OffsetDateTime.now(),
                null);
    }

    public static ApiErrorResponse of(
            String businessCode,
            String requestPath,
            String message,
            Map<String, String> fieldErrors) {
        Map<String, String> copy =
                fieldErrors == null ? null : Collections.unmodifiableMap(fieldErrors);
        String msg =
                message != null
                        ? message
                        : (businessCode != null ? businessCode : ErrorCodes.STORE_HTTP_STATUS.businessCode());
        return new ApiErrorResponse(requestPath, businessCode, msg, OffsetDateTime.now(), copy);
    }
}
