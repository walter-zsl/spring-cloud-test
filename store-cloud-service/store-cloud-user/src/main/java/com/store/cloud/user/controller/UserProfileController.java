package com.store.cloud.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.cloud.core.response.api.ApiEnvelope;
import com.store.cloud.user.api.dto.UserProfileVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "用户 JWT 资料")
public class UserProfileController {

    @Operation(summary = "当前 JWT 主体摘要（sub / issuer）")
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/api/v1/users/me")
    public ApiEnvelope<UserProfileVO> me(@AuthenticationPrincipal Jwt jwt) {
        UserProfileVO vo = new UserProfileVO(
                jwt.getSubject(),
                jwt.getIssuer() != null ? jwt.getIssuer().toString() : "");
        return ApiEnvelope.ok(vo);
    }

    /** 与 {@code UserPublicFeignClient}、网关探测路径一致；无 JWT。 */
    @Operation(summary = "用户服务可达性探测", tags = {"公开探测"})
    @GetMapping("/api/public/ping")
    public ApiEnvelope<String> ping() {
        return ApiEnvelope.ok("ok-user");
    }
}
