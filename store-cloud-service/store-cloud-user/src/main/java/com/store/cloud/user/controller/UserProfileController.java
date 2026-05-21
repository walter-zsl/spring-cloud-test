package com.store.cloud.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.cloud.user.api.dto.UserProfileVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户 JWT 资料")
@SecurityRequirement(name = "bearer-jwt")
public class UserProfileController {

    @Operation(summary = "当前 JWT 主体摘要（sub / issuer）")
    @GetMapping("/me")
    public UserProfileVO me(@AuthenticationPrincipal Jwt jwt) {
        return new UserProfileVO(
                jwt.getSubject(),
                jwt.getIssuer() != null ? jwt.getIssuer().toString() : "");
    }
}
