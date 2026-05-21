package com.store.cloud.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.store.cloud.auth.api.dto.FetchSmsCodeRequest;
import com.store.cloud.auth.service.SmsService;
import com.store.cloud.core.response.api.ApiEnvelope;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "认证 / OAuth2-JWT")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @Operation(summary = "获取短信验证码", description = "占位：后续接入短信网关；成功数据为服务端提示文案。")
    @PostMapping(value = "/api/auth/fetch-sms-code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiEnvelope<String> fetchSmsCode(@Validated @RequestBody FetchSmsCodeRequest request) {
        return ApiEnvelope.ok(smsService.sendSmsCode(request.mobile().trim()));
    }
}
