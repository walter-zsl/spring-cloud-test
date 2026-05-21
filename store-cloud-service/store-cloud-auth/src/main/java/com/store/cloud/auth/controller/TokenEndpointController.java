package com.store.cloud.auth.controller;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.store.cloud.auth.dto.LoginRequest;
import com.store.cloud.auth.service.AuthService;
import com.store.cloud.core.security.oauth2.OAuth2AccessTokenBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "认证 / OAuth2-JWT")
public class TokenEndpointController {

    private static final String PASSWORD_GRANT = "password";

    private final AuthService authService;

    public TokenEndpointController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "JSON 登录", description = "兼容前端 JSON Body；响应为 OAuth2 snake_case：`access_token` / `token_type` / `expires_in`。")
    @PostMapping(value = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessTokenBody loginJson(@Valid @RequestBody LoginRequest request) {
        return authService.issuePasswordGrant(request);
    }

    @Operation(summary = "OAuth2 Token Endpoint（password）", description = "RFC 6749；`grant_type` 必须为 `password`，附加 `username`、`password`，Content-Type：`application/x-www-form-urlencoded`。")
    @PostMapping(value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public OAuth2AccessTokenBody tokenForm(
            @RequestParam(name = "grant_type") String grantType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password) {

        if (grantType == null || !PASSWORD_GRANT.equalsIgnoreCase(grantType.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported_grant_type");
        }
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(Locale.ROOT, "invalid_request:missing_%s",
                            username == null || username.isBlank() ? "username" : "password"));
        }
        return authService.issuePasswordGrant(username, password);
    }
}
