package com.store.cloud.auth.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/** 认证服务公开接口（无需 Token），用于联调/健康探测。 */
@FeignClient(name = "store-auth-service", contextId = "authPublicFeignClient")
public interface AuthPublicFeignClient {

    @GetMapping("/api/public/ping")
    String ping();
}
