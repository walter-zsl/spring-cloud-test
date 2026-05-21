package com.store.cloud.auth.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.store.cloud.auth.api.dto.AccessTokenResponse;
import com.store.cloud.auth.api.dto.LoginRequest;

/** 通过 Feign 调用认证中心的 JSON 登录（与 {@code POST /api/auth/login} 对齐）。 */
@FeignClient(name = "store-auth-service", contextId = "authTokenFeignClient")
public interface AuthTokenFeignClient {

    @PostMapping(value = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    AccessTokenResponse login(@RequestBody LoginRequest request);
}
