package com.store.cloud.auth.api.dto;

/**
 * JSON 登录请求体（与 {@code POST /api/auth/login} 对齐）。
 * <p>校验注解放在各服务端实现；本契约模块保持仅数据结构。
 */
public record LoginRequest(String username, String password) {}
