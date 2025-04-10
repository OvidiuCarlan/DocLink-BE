package com.example.doclink.business.cases;

import com.example.doclink.business.dto.LoginRequest;
import com.example.doclink.business.dto.LoginResponse;

public interface LoginUseCase {
    LoginResponse login(LoginRequest loginRequest);
}