package com.store.cloud.user.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.store.cloud.user.api.dto.UserProfileVO;

/**
 * 调用用户服务「需登录」接口。调用方需透传 {@code Authorization: Bearer ...}。
 */
@FeignClient(name = "store-user-service", contextId = "userProfileFeignClient", path = "/api/v1/users")
public interface UserProfileFeignClient {

    @GetMapping("/me")
    UserProfileVO getMe(@RequestHeader("Authorization") String authorization);
}
