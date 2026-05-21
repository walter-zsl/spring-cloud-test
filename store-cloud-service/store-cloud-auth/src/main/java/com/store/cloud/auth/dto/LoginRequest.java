package com.store.cloud.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Schema(example = "demo", description = "登录名（bootstrap：application-local.yml 中配置的账号之一）") String username,
        @NotBlank @Schema(example = "demo", description = "明文口令（已由 PasswordEncoder/BootstrapAccount 对齐）") String password) {}
