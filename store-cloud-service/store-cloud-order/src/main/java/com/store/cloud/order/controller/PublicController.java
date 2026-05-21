package com.store.cloud.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "公开探测")
public class PublicController {

    @Operation(summary = "订单服务就绪探测")
    @GetMapping("/api/public/health")
    public String health() {
        return "ok";
    }
}
