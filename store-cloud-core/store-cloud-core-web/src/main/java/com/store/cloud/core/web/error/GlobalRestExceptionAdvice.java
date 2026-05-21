package com.store.cloud.core.web.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import com.store.cloud.core.logging.MdcFieldNames;
import com.store.cloud.core.logging.autoconfigure.StoreCloudLoggingProperties;

/**
 * MVC 层异常统一为 {@link ApiErrorResponse}。<b>成功外层</b>见 {@code com.store.cloud.core.response.api}。
 */
@RestControllerAdvice
public class GlobalRestExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionAdvice.class);

    private final StoreCloudLoggingProperties loggingProperties;

    public GlobalRestExceptionAdvice(ObjectProvider<StoreCloudLoggingProperties> loggingProperties) {
        this.loggingProperties = loggingProperties.getIfAvailable();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> onValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        String path = uri(req);
        String summary = "请求参数校验未通过";
        String traceId = traceIdFromMdc();
        log.warn("REST validation failed path={} traceId={} fields={}", path, traceId != null ? traceId : "-", fields);
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_VALIDATION_FAILED.businessCode(), path, summary, fields, traceId);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> onBusiness(BusinessException ex, HttpServletRequest req) {
        ApiErrorResponse body = ApiErrorResponse.of(ex.category(), uri(req), ex.getMessage(), traceIdFromMdc());
        return ResponseEntity.status(ex.httpStatusCode()).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    /** 来自 {@link org.springframework.web.server.ResponseStatusException}。 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> onResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        String reason = ex.getReason() != null ? ex.getReason() : ErrorCodes.STORE_HTTP_STATUS.businessCode();
        ApiErrorResponse body =
                ApiErrorResponse.of(
                        ErrorCodes.STORE_HTTP_STATUS.businessCode(),
                        uri(req),
                        reason,
                        null,
                        traceIdFromMdc());
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(ex.getHeaders() != null ? ex.getHeaders() : HttpHeaders.EMPTY)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> onAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ApiErrorResponse body =
                ApiErrorResponse.of(
                        ErrorCodes.STORE_FORBIDDEN,
                        uri(req),
                        ErrorCodes.STORE_FORBIDDEN.businessCode(),
                        traceIdFromMdc());
        return ResponseEntity.status(ErrorCodes.STORE_FORBIDDEN.httpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> onIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_VALIDATION_FAILED, uri(req), ex.getMessage(), traceIdFromMdc());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> onUncaught(RuntimeException ex, HttpServletRequest req) {
        String traceId = traceIdFromMdc();
        log.error("Unhandled runtime exception path={} traceId={}", uri(req), traceId != null ? traceId : "-", ex);
        ApiErrorResponse body =
                ApiErrorResponse.of(
                        ErrorCodes.STORE_INTERNAL_SERVER_ERROR,
                        uri(req),
                        ErrorCodes.STORE_INTERNAL_SERVER_ERROR.businessCode(),
                        traceId);
        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    /** 与会话链路一致：默认键 {@link MdcFieldNames#TRACE_ID}，可被 {@code store.logging.mdc-trace-id-key} 覆盖。 */
    private String traceIdFromMdc() {
        String key = loggingProperties != null ? loggingProperties.getMdcTraceIdKey() : MdcFieldNames.TRACE_ID;
        String raw = MDC.get(key);
        return raw != null && !raw.isBlank() ? raw.trim() : null;
    }

    private static String uri(HttpServletRequest req) {
        return req != null ? req.getRequestURI() : null;
    }
}
