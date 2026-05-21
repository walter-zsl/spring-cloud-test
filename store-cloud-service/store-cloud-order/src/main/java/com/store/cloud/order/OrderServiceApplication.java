package com.store.cloud.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单服务。<b>扫包</b>：仅 {@code com.store.cloud.order}；共用能力通过 {@code store-cloud-core-*} 自动配置导入。
 */
@SpringBootApplication(scanBasePackages = "com.store.cloud.order")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
