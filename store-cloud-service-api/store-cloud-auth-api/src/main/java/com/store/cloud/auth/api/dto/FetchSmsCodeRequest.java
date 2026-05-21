package com.store.cloud.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/** JSON 获取短信请求体（与 {@code POST /api/fetch-sms-code} 对齐）。 */
public record FetchSmsCodeRequest(@NotBlank String mobile) {}