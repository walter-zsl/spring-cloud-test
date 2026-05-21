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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * <b>Outbound（右侧）</b>：登录、客户端凭证等校验<strong>成功后</strong>，用对称密钥 HS256 生成 JWT 字符串。
 * <p>由 {@code store.security.jwt.issue-tokens} 控制；业务资源服务（只做 Bearer 校验）应为 {@code false}。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "store.security.jwt", name = "issue-tokens", havingValue = "true", matchIfMissing = true)
public class JwtEncoderServletConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtEncoder.class)
    JwtEncoder storeCloudJwtEncoder(StoreSecurityProperties props) {
        SecretKey sk = toSecretKey(props.getJwt().getSecret());
        return NimbusJwtEncoder.withSecretKey(sk).algorithm(MacAlgorithm.HS256).build();
    }

    private static SecretKey toSecretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
