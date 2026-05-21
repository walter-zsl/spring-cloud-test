package com.store.cloud.auth.service;

import com.store.cloud.auth.dto.LoginRequest;
import com.store.cloud.core.security.oauth2.OAuth2AccessTokenBody;

public interface AuthService {

    OAuth2AccessTokenBody issuePasswordGrant(LoginRequest request);

    OAuth2AccessTokenBody issuePasswordGrant(String username, String password);
}
