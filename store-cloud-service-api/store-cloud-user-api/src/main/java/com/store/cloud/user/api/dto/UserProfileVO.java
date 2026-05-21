package com.store.cloud.user.api.dto;

/** 当前登录用户简要信息（与 /api/v1/users/me 对齐）。 */
public record UserProfileVO(String subject, String issuer) {}
