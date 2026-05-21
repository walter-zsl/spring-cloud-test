package com.store.cloud.core.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * <b>Inbound（左侧）</b>：把 {@code Bearer} JWT 转成 Spring Security 可识别的 {@link org.springframework.security.oauth2.jwt.Jwt}，
 * 供 {@link org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer} 链使用。
 * <p>同配置内注册默认 {@link JwtAuthenticationConverter}，避免与 {@link JwtDecoder} 分属两个 {@code Configuration} 时
 * {@code @ConditionalOnBean(JwtDecoder)} 解析次序导致<strong>永远不装配</strong> Converter。
 * <p>若不装载本配置（例如认证中心 {@code validate-incoming-jwt=false}），则 Spring Boot 不会因「存在 JwtDecoder」而挂上默认的 OAuth2 Resource
 * Server 安全链——避免与手写登录链<strong>双线冲突</strong>导致「认证服务起不来」。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "store.security.jwt",
        name = "validate-incoming-jwt",
        havingValue = "true",
        matchIfMissing = true)
public class JwtDecoderServletConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder storeCloudJwtDecoder(StoreSecurityProperties props) {
        SecretKey secretKey = toSecretKey(props.getJwt().getSecret());
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    JwtAuthenticationConverter storeCloudJwtAuthenticationConverter() {
        return JwtAuthenticationConverters.defaultJwtAuthenticationConverter();
    }

    private static SecretKey toSecretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
