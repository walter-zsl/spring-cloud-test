package com.store.cloud.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务。<b>扫包</b>：仅 {@code com.store.cloud.user}；共用能力见 {@code store-cloud-core-*}
 * （通过 Boot 自动配置导入，不把 {@code com.store.cloud.core} 整包塞进扫描）。
 */
@SpringBootApplication(scanBasePackages = "com.store.cloud.user")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
