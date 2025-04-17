package com.example.doclink.business.cases;

import com.example.doclink.business.dto.CreateUserRequest;
import com.example.doclink.business.dto.CreateUserResponse;

public interface CreateUserUseCase {
    CreateUserResponse createUser(CreateUserRequest request);
}
