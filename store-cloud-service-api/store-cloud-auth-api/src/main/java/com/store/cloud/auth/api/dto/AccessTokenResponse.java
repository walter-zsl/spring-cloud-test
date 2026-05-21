package com.store.cloud.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuth 2 访问令牌成功响应（RFC 6749 §5.1），JSON 字段 snake_case；
 * 供 Feign 调用认证服务反序列化（与 core 中签发体字段一致）。
 */
public record AccessTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresInSeconds) {}
