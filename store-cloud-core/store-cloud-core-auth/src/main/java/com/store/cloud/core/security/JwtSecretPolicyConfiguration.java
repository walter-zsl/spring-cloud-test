package com.store.cloud.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

/**
 * <b>对称密钥策略（左右共用）</b>：无论本 JVM 只做「验签」还是「签发」或两者兼有，只要加载了本 Jar 的 Servlet 套件就会执行。
 * <p>左侧验签、右侧签发<strong>必须使用同一段 UTF-8 密钥</strong>（配置为 {@code store.security.jwt.secret} / 环境变量 {@code JWT_SECRET}），
 * 否则业务服务无法解析认证中心签出的 Token。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JwtSecretPolicyConfiguration {

    private static final String WEAK_BUILTIN_SUBSTRING = "please-change-this-secret";

    private final StoreSecurityProperties props;

    public JwtSecretPolicyConfiguration(StoreSecurityProperties props) {
        this.props = props;
    }

    @PostConstruct
    void enforceJwtCryptographyPolicies() {
        StoreSecurityProperties.JwtSettings jwt = props.getJwt();
        String secret = jwt.getSecret();
        Objects.requireNonNull(secret, "store.security.jwt.secret");
        byte[] utf8 = secret.getBytes(StandardCharsets.UTF_8);
        if (utf8.length < 32) {
            throw new IllegalArgumentException("store.security.jwt.secret UTF-8 长度须 ≥32 字节");
        }
        if (jwt.isEnforceStrongSecret()) {
            if (secret.contains(WEAK_BUILTIN_SUBSTRING)) {
                throw new IllegalArgumentException(
                        "禁止使用内置占位密钥，请注入强随机密钥（如环境变量 JWT_SECRET / 配置中心）");
            }
        }
    }
}
