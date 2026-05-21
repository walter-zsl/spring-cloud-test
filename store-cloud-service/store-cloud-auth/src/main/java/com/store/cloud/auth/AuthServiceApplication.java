package com.store.cloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证中心启动类。
 *
 * <p><b>组件扫描</b>：仅扫描本模块 {@code com.store.cloud.auth.*}。
 * <b>Servlet/JWT、统一出错 JSON：</b>{@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * 分别由 {@code store-cloud-core-auth}、{@code store-cloud-core-web} 提供；{@code store-cloud-core-response}
 * （成功外层契约）仅作传递依赖<strong>不带</strong> AutoConfiguration，
 * 无需扩大 {@code scanBasePackages}。</p>
 */
@SpringBootApplication(scanBasePackages = "com.store.cloud.auth")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
