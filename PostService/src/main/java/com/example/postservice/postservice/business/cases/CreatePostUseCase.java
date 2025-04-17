package com.example.postservice.postservice.business.cases;

import com.example.postservice.postservice.business.dto.CreatePostRequest;
import com.example.postservice.postservice.business.dto.CreatePostResponse;

public interface CreatePostUseCase {
    CreatePostResponse createPost(CreatePostRequest request);
}
