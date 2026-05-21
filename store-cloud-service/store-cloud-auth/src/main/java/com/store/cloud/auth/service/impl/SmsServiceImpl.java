package com.store.cloud.auth.service.impl;

import org.springframework.stereotype.Service;
import com.store.cloud.auth.service.SmsService;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public String sendSmsCode(String mobile) {
        return "123456";
    }
    
}
