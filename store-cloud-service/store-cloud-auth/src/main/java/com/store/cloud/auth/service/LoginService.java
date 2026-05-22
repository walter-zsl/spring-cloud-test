package com.store.cloud.auth.service;

import com.store.cloud.auth.api.dto.LoginRequest;

public interface LoginService {
    
    String fetchUserToken(LoginRequest request);
}
