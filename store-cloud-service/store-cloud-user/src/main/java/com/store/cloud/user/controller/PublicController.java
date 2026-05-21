package com.store.cloud.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "公开探测")
public class PublicController {

    @Operation(summary = "用户服务可达性探测")
    @GetMapping("/api/public/ping")
    public String ping() {
        return "ok";
    }
}
