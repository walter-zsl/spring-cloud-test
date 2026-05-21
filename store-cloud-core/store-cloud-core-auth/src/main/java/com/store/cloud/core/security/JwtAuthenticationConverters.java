package com.store.cloud.core.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * <b>左侧 Inbound 辅助</b>：声明「JWT 里哪一段 claim 对应 Spring 的权限」。
 * <p>默认：读 claim {@code roles}，且<strong>不</strong>再自动加 {@code ROLE_} 前缀（Token 里已是完整角色名如 {@code ROLE_USER}）。
 */
public final class JwtAuthenticationConverters {

    private JwtAuthenticationConverters() {}

    public static JwtAuthenticationConverter defaultJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        granted.setAuthorityPrefix("");
        granted.setAuthoritiesClaimName("roles");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(granted);
        return converter;
    }
}
