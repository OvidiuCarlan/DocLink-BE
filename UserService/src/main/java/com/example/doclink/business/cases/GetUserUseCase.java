package com.example.doclink.business.cases;

import com.example.doclink.business.dto.GetUserResponse;

public interface GetUserUseCase {
    GetUserResponse getUserById(Long userId);
}
