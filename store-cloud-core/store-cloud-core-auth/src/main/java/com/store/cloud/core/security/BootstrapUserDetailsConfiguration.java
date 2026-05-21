package com.store.cloud.core.security;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * <b>Inbound — 第一道门（用户名+口令）</b>：在未对接真实用户库前，可从 YAML（或配置中心映射进 {@link StoreSecurityProperties}）
 * 读出若干「Bootstrap 演示账号」，供表单/JSON 登录走 {@link org.springframework.security.authentication.AuthenticationManager}。
 * <ul>
 *     <li>适用侧：<strong>仅认证中心 {@code store-cloud-auth}</strong> 应长期打开；其它业务 JVM 一般用 {@link StoreSecurityProperties.UserSource#custom}，
 *         避免误把演示账号带进订单、库存等 Jar。</li>
 *     <li>与 Bearer（JWT）关系：本条链解决「如何认出 demo 口令」；签发 JWT 在右侧 {@link StoreCloudJwtAccessTokenIssuer}。</li>
 * </ul>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "store.security", name = "user-source", havingValue = "bootstrap")
@ConditionalOnMissingBean(UserDetailsService.class)
public class BootstrapUserDetailsConfiguration {

    @Bean
    public UserDetailsService storeCloudBootstrapUserDetailsService(
            StoreSecurityProperties properties, Environment environment) {
        if (properties.getBootstrapUsers().isEmpty()) {
            throw new IllegalStateException(
                    "store.security.bootstrap-users 不能为空：请先配置 YAML/Apollo/K8s，或将 store.security.user-source 改为 custom 并自定义 UserDetailsService Bean。");
        }
        boolean prod =
                Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("prod"));

        UserDetails[] details = properties.getBootstrapUsers().stream()
                .filter(a -> a.getUsername() != null && !a.getUsername().isBlank())
                .peek(a -> {
                    if (a.getPasswordEncoded() == null || a.getPasswordEncoded().isBlank()) {
                        throw new IllegalStateException("bootstrap-users 缺少 password-encoded：" + a.getUsername());
                    }
                    if (prod && a.getPasswordEncoded().contains("{noop}")) {
                        throw new IllegalStateException(
                                "生产环境禁止使用 {noop} 口令，请使用 {bcrypt} 等：" + a.getUsername());
                    }
                })
                .map(account -> User.builder()
                        .username(account.getUsername())
                        .password(account.getPasswordEncoded())
                        .roles(resolveRoles(account))
                        .build())
                .toArray(UserDetails[]::new);

        return new InMemoryUserDetailsManager(details);
    }

    private static String[] resolveRoles(StoreSecurityProperties.BootstrapAccount account) {
        if (account.getRoles() == null || account.getRoles().isEmpty()) {
            return new String[] {"USER"};
        }
        return account.getRoles().toArray(String[]::new);
    }
}
