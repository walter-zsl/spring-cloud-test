package com.store.cloud.core.web.error;

import org.springframework.http.HttpStatusCode;

/** 明确的业务语义失败（非 JVM 运行时异常）；由全局 Handler 编成统一 JSON。 */
public final class BusinessException extends RuntimeException {

    private final ErrorCodes category;
    private final HttpStatusCode statusOverride;

    public BusinessException(ErrorCodes category, String message) {
        super(message != null ? message : category.businessCode());
        this.category = category;
        this.statusOverride = category.httpStatus();
    }

    public BusinessException(ErrorCodes category, HttpStatusCode statusOverride, String message) {
        super(message != null ? message : category.businessCode());
        this.category = category;
        this.statusOverride = statusOverride != null ? statusOverride : category.httpStatus();
    }

    public BusinessException(ErrorCodes category, Throwable cause) {
        super(category.businessCode(), cause);
        this.category = category;
        this.statusOverride = category.httpStatus();
    }

    public ErrorCodes category() {
        return category;
    }

    public HttpStatusCode httpStatusCode() {
        return statusOverride;
    }
}
