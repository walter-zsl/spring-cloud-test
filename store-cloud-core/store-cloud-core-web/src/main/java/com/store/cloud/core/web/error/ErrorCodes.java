package com.store.cloud.core.web.error;

import org.springframework.http.HttpStatus;

/**
 * 全局业务 HTTP 语义与机器可读码前缀 {@code STORE_*}（可按域继续细分）。
 */
public enum ErrorCodes {
    STORE_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "STORE_VALIDATION_FAILED"),

    STORE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "STORE_UNAUTHORIZED"),

    STORE_FORBIDDEN(HttpStatus.FORBIDDEN, "STORE_FORBIDDEN"),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND"),

    STORE_CONFLICT(HttpStatus.CONFLICT, "STORE_CONFLICT"),

    /** 口令错误等，与 OAuth {@code invalid_grant} 语义对齐 */
    STORE_AUTH_INVALID_GRANT(HttpStatus.UNAUTHORIZED, "STORE_AUTH_INVALID_GRANT"),

    /** Token Endpoint：{@code grant_type} 不支持 */
    STORE_AUTH_UNSUPPORTED_GRANT(HttpStatus.BAD_REQUEST, "STORE_AUTH_UNSUPPORTED_GRANT"),

    /** Token Endpoint：缺少 username/password 等 */
    STORE_AUTH_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "STORE_AUTH_INVALID_REQUEST"),

    /** 透传 MVC 语义；HTTP 状态由 {@link org.springframework.web.server.ResponseStatusException} 等决定 */
    STORE_HTTP_STATUS(HttpStatus.BAD_REQUEST, "STORE_HTTP_STATUS"),

    STORE_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "STORE_INTERNAL_SERVER_ERROR");

    private final HttpStatus httpStatus;

    private final String businessCode;

    ErrorCodes(HttpStatus httpStatus, String businessCode) {
        this.httpStatus = httpStatus;
        this.businessCode = businessCode;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String businessCode() {
        return businessCode;
    }
}
