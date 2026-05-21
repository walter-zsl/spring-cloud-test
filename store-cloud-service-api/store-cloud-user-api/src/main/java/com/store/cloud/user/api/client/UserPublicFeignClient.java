package com.store.cloud.user.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/** 用户服务公开接口（无需 Token），用于联调/健康探测。 */
@FeignClient(name = "store-user-service", contextId = "userPublicFeignClient")
public interface UserPublicFeignClient {

    @GetMapping("/api/public/ping")
    String ping();
}
