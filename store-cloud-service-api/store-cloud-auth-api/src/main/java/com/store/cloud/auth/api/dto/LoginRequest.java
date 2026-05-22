package com.store.cloud.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Schema(example = "mobile", description = "用户号码") String mobile,
        @NotBlank @Schema(example = "code", description = "短信验证密码") String code,
        @NotBlank @Schema(example = "scope", description = "中间件名称") String scope) {}
