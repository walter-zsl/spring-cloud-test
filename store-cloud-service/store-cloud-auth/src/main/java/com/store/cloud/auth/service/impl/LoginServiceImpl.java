package com.store.cloud.auth.service.impl;

import org.springframework.stereotype.Service;

import com.store.cloud.auth.api.dto.LoginRequest;
import com.store.cloud.auth.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {
    
    @Override
    public String fetchUserToken(LoginRequest request) {
        return null;
    }
}