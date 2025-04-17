package com.example.postservice.postservice.business.impl;

import com.example.postservice.postservice.business.cases.CreatePostUseCase;
import com.example.postservice.postservice.business.dto.CreatePostRequest;
import com.example.postservice.postservice.business.dto.CreatePostResponse;
import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CratePostUseCaseImpl implements CreatePostUseCase {

    private final PostRepository postRepository;
    @Override
    public CreatePostResponse createPost(CreatePostRequest request) {
        PostEntity savedPost = savedNewPost(request);

        return CreatePostResponse.builder()
                .postId(Long.valueOf(savedPost.getId()))
                .build();
    }

    private PostEntity savedNewPost(CreatePostRequest request){

        PostEntity newPost = PostEntity.builder()
                .userId(request.getUserId())
                .title(request.getContent())
                .content(request.getContent())
                .category(request.getCategory())
                .build();
        return postRepository.save(newPost);
    }
}
