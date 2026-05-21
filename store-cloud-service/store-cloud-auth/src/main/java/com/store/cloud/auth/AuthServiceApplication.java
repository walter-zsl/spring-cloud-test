package com.store.cloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证中心启动类。
 * <p><b>组件扫描</b>：{@code com.store.cloud} 会扫到 {@code com.store.cloud.core.security} 下的
 * {@link com.store.cloud.core.security.StoreCloudServletSecurityToolkitConfiguration}（提供 JWT 签发、Bootstrap 用户等），
 * 以及本模块 {@code com.store.cloud.auth} 下的 Controller/Service。
 * <p><b>必要配置</b>：{@code store.security.jwt.validate-incoming-jwt=false}，避免 core-auth 引入的
 * {@code oauth2-resource-server} 因存在 {@code JwtDecoder} 而自动装配第二条安全链，导致本服务无法启动。
 */
@SpringBootApplication(scanBasePackages = "com.store.cloud")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
