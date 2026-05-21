package com.store.cloud.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.store.cloud.auth.dto.LoginRequest;
import com.store.cloud.auth.service.AuthService;
import com.store.cloud.core.security.StoreCloudJwtAccessTokenIssuer;
import com.store.cloud.core.security.oauth2.OAuth2AccessTokenBody;

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
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_grant");
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_grant");
        }
    }
}
