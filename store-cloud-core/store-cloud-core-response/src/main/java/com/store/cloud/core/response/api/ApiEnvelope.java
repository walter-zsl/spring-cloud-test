package com.store.cloud.core.response.api;

import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一「成功」外层 JSON（camelCase）。出错时优先在各 Servlet 应用中由
 * {@link com.store.cloud.core.web.error.ApiErrorResponse} + HTTP 状态码表达（见 {@code store-cloud-core-web}）。 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiEnvelope<T>(
        Boolean success,
        String code,
        String message,
        T data,
        OffsetDateTime timestamp,
        String traceId,
        Map<String, Object> extra) {

    public static <T> ApiEnvelope<T> ok(T data) {
        return success(
                true,
                ApiEnvelopeConstants.SUCCESS_CODE_REST,
                ApiEnvelopeConstants.MESSAGE_OK,
                data,
                null,
                null);
    }

    public static ApiEnvelope<Void> okVoid() {
        return okVoid(ApiEnvelopeConstants.MESSAGE_OK, null);
    }

    public static ApiEnvelope<Void> okVoid(String message, String traceId) {
        return success(true, ApiEnvelopeConstants.SUCCESS_CODE_REST, message, null, traceId, null);
    }

    public static <T> ApiEnvelope<T> okLegacyZero(T data) {
        return success(
                true,
                ApiEnvelopeConstants.SUCCESS_CODE_LEGACY_ZERO,
                ApiEnvelopeConstants.MESSAGE_SUCCESS_DEFAULT,
                data,
                null,
                null);
    }

    public static <T> ApiEnvelope<PagedPayload<T>> okPage(PagedPayload<T> pagePayload) {
        return ok(pagePayload);
    }

    public static <T> ApiEnvelope<T> success(
            Boolean success,
            String code,
            String message,
            T data,
            String traceId,
            Map<String, Object> extra) {
        Boolean s = success != null ? success : deriveSuccessPredicate(code);
        return new ApiEnvelope<>(
                s,
                code,
                message != null ? message : code,
                data,
                OffsetDateTime.now(),
                traceId,
                extra);
    }

    public static <T> ApiEnvelope<T> failed(String businessCode, String message, T dataHint) {
        return new ApiEnvelope<>(false, businessCode, message, dataHint, OffsetDateTime.now(), null, null);
    }

    public static <T> ApiEnvelope<T> failed(String businessCode, String message, T dataHint, String traceId) {
        return new ApiEnvelope<>(false, businessCode, message, dataHint, OffsetDateTime.now(), traceId, null);
    }

    private static boolean deriveSuccessPredicate(String code) {
        if (code == null) {
            return false;
        }
        String c = code.trim();
        return ApiEnvelopeConstants.SUCCESS_CODE_REST.equalsIgnoreCase(c)
                || ApiEnvelopeConstants.SUCCESS_CODE_LEGACY_ZERO.equals(c)
                || "200".equals(c);
    }
}
