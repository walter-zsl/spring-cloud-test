package com.store.cloud.core.web.error;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

/**
 * 业务数字化错误/成功编码（与 HTTP 状态码各司其职）。约定：
 * <ul>
 *     <li><b>{@value #SUCCESS_TIER_MIN} ≤ code &lt; {@value #ERROR_TIER_MIN}</b> — 标识正确（成功语义，可与外层
 *             {@link com.store.cloud.core.response.api.ApiEnvelope} 对齐）</li>
 *     <li><b>code ≥ {@value #ERROR_TIER_MIN}</b> — 标识错误</li>
 * </ul>
 */
public enum ErrorCodes {

    /** 通用成功编码（可选用作 {@link com.store.cloud.core.response.api.ApiEnvelope#code()}） */
    SUCCESS(HttpStatus.OK, 20000, "success"),

    STORE_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, 40000, "请求参数校验未通过"),

    STORE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40001, "未授权"),

    STORE_FORBIDDEN(HttpStatus.FORBIDDEN, 40002, "禁止访问"),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 40003, "资源不存在"),

    STORE_CONFLICT(HttpStatus.CONFLICT, 40004, "资源冲突"),

    /** 口令错误等，与 OAuth {@code invalid_grant} 语义对齐 */
    STORE_AUTH_INVALID_GRANT(HttpStatus.UNAUTHORIZED, 40005, "invalid_grant"),

    /** Token Endpoint：{@code grant_type} 不支持 */
    STORE_AUTH_UNSUPPORTED_GRANT(HttpStatus.BAD_REQUEST, 40006, "unsupported_grant_type"),

    /** Token Endpoint：缺少 username/password 等 */
    STORE_AUTH_INVALID_REQUEST(HttpStatus.BAD_REQUEST, 40007, "invalid_request"),

    /** 透传 MVC 语义；HTTP 状态由 {@link org.springframework.web.server.ResponseStatusException} 等决定 */
    STORE_HTTP_STATUS(HttpStatus.BAD_REQUEST, 40008, "请求未完成"),

    STORE_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 40009, "内部服务器错误");

    /** 成功码段下限（含）。 */
    public static final int SUCCESS_TIER_MIN = 20000;

    /** 错误码段下限（含）。其上均为错误语义。 */
    public static final int ERROR_TIER_MIN = 40000;

    private static final Map<Integer, ErrorCodes> BY_NUMERIC_CODE =
            Arrays.stream(ErrorCodes.values()).collect(Collectors.toUnmodifiableMap(ErrorCodes::code, Function.identity()));

    private final HttpStatus httpStatus;
    private final int code;
    private final String defaultMessage;

    ErrorCodes(HttpStatus httpStatus, int code, String defaultMessage) {
        Objects.requireNonNull(httpStatus);
        Objects.requireNonNull(defaultMessage);
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    /** 数字业务码（JSON）。 */
    public int code() {
        return code;
    }

    /**
     * {@link ApiErrorResponse} / {@link BusinessException} 等在未提供自定义文案时的默认说明。
     */
    public String defaultMessage() {
        return defaultMessage;
    }

    /** 是否落在<b>正确</b>码段 [{@link #SUCCESS_TIER_MIN}, {@link #ERROR_TIER_MIN})。 */
    public boolean successTier() {
        return code >= SUCCESS_TIER_MIN && code < ERROR_TIER_MIN;
    }

    /** 是否落在<b>错误</b>码段 [{@link #ERROR_TIER_MIN}, +∞)。 */
    public boolean errorTier() {
        return code >= ERROR_TIER_MIN;
    }

    public static Optional<ErrorCodes> fromNumericCode(int numericCode) {
        return Optional.ofNullable(BY_NUMERIC_CODE.get(numericCode));
    }
}
