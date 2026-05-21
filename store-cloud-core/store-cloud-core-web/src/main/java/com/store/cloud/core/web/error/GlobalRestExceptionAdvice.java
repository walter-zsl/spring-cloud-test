package com.store.cloud.core.web.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * MVC 层异常统一为 {@link ApiErrorResponse}。<b>成功外层</b>见 {@code com.store.cloud.core.response.api}。
 */
@RestControllerAdvice
public class GlobalRestExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> onValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        String path = uri(req);
        String summary = "请求参数校验未通过";
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_VALIDATION_FAILED.businessCode(), path, summary, fields);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> onBusiness(BusinessException ex, HttpServletRequest req) {
        ApiErrorResponse body = ApiErrorResponse.of(ex.category(), uri(req), ex.getMessage());
        return ResponseEntity.status(ex.httpStatusCode()).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    /** 来自 {@link org.springframework.web.server.ResponseStatusException}。 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> onResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        String reason = ex.getReason() != null ? ex.getReason() : ErrorCodes.STORE_HTTP_STATUS.businessCode();
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_HTTP_STATUS.businessCode(), uri(req), reason, null);
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(ex.getHeaders() != null ? ex.getHeaders() : HttpHeaders.EMPTY)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> onAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_FORBIDDEN, uri(req), ErrorCodes.STORE_FORBIDDEN.businessCode());
        return ResponseEntity.status(ErrorCodes.STORE_FORBIDDEN.httpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> onIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ApiErrorResponse body =
                ApiErrorResponse.of(ErrorCodes.STORE_VALIDATION_FAILED, uri(req), ex.getMessage());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> onUncaught(RuntimeException ex, HttpServletRequest req) {
        log.error("Unhandled runtime exception path={}", uri(req), ex);
        ApiErrorResponse body =
                ApiErrorResponse.of(
                        ErrorCodes.STORE_INTERNAL_SERVER_ERROR,
                        uri(req),
                        ErrorCodes.STORE_INTERNAL_SERVER_ERROR.businessCode());
        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private static String uri(HttpServletRequest req) {
        return req != null ? req.getRequestURI() : null;
    }
}
