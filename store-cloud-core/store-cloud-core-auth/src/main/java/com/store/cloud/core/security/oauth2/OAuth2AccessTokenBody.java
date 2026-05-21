package com.store.cloud.core.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b>Outbound（右侧返回体）</b>：面向 HTTP 客户端的 OAuth 2 访问令牌成功响应（RFC 6749 §5.1），JSON 字段用 snake_case。
 * <p>左侧「带上来」的是用户名口令或授权码；右侧「发下去」的是 access_token 三件套。
 */
public record OAuth2AccessTokenBody(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresInSeconds) {}
