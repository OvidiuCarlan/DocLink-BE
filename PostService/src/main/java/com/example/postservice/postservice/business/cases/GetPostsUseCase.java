package com.example.postservice.postservice.business.cases;

import com.example.postservice.postservice.business.dto.GetPostsRequest;
import com.example.postservice.postservice.business.dto.GetPostsResponse;

public interface GetPostsUseCase {
    GetPostsResponse getPosts(GetPostsRequest request);
}
