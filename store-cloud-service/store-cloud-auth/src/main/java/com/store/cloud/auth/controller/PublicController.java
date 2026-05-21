package com.store.cloud.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.cloud.core.response.api.ApiEnvelope;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "健康 / 连通性")
public class PublicController {

    @Operation(summary = "认证中心可达性探测", description = "统一外层 ApiEnvelope，`data` 为简短标识字符串。")
    @GetMapping("/api/public/ping")
    public ApiEnvelope<String> ping() {
        return ApiEnvelope.ok("ok-auth");
    }
}
