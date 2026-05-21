package com.store.cloud.core.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import com.store.cloud.core.security.oauth2.OAuth2AccessTokenBody;

/**
 * <b>Outbound（右侧）</b>：把 Spring Security 已认证成功的 {@link Authentication} 映射为「可返回给前端」的标准 OAuth2 Body（含 JWT 字符串）。
 * <p>调用方通常为认证中心控制器 / Service（左侧口令校验已由 {@link org.springframework.security.authentication.AuthenticationManager}
 * 与用户体系完成）。
 *
 * @see JwtEncoderServletConfiguration 提供 JwtEncoder Bean
 */
@Component
@ConditionalOnBean(JwtEncoder.class)
public class StoreCloudJwtAccessTokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final StoreSecurityProperties storeSecurityProperties;

    public StoreCloudJwtAccessTokenIssuer(JwtEncoder jwtEncoder, StoreSecurityProperties storeSecurityProperties) {
        this.jwtEncoder = jwtEncoder;
        this.storeSecurityProperties = storeSecurityProperties;
    }

    public OAuth2AccessTokenBody issue(Authentication authentication) {
        StoreSecurityProperties.JwtSettings jwt = storeSecurityProperties.getJwt();
        Jwt token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer(jwt.getIssuer())
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plus(jwt.getExpirationMinutes(), ChronoUnit.MINUTES))
                        .subject(authentication.getName())
                        .claim(
                                "roles",
                                authentication.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList()))
                        .build()));
        long seconds = jwt.getExpirationMinutes() * 60;
        return new OAuth2AccessTokenBody(token.getTokenValue(), "Bearer", seconds);
    }
}
