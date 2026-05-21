package com.store.cloud.auth.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.store.cloud.auth.dto.LoginRequest;
import com.store.cloud.auth.service.AuthService;
import com.store.cloud.core.security.StoreCloudJwtAccessTokenIssuer;
import com.store.cloud.core.security.oauth2.OAuth2AccessTokenBody;
import com.store.cloud.core.web.error.BusinessException;
import com.store.cloud.core.web.error.ErrorCodes;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final StoreCloudJwtAccessTokenIssuer jwtAccessTokenIssuer;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager, StoreCloudJwtAccessTokenIssuer jwtAccessTokenIssuer) {
        this.authenticationManager = authenticationManager;
        this.jwtAccessTokenIssuer = jwtAccessTokenIssuer;
    }

    @Override
    public OAuth2AccessTokenBody issuePasswordGrant(LoginRequest request) {
        return issuePasswordGrant(request.username(), request.password());
    }

    @Override
    public OAuth2AccessTokenBody issuePasswordGrant(String username, String password) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtAccessTokenIssuer.issue(authentication);
        } catch (AuthenticationException ex) {
            throw new BusinessException(ErrorCodes.STORE_AUTH_INVALID_GRANT, "invalid_grant");
        }
    }
}
