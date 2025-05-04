package com.example.postservice.postservice.business.impl;


import com.example.postservice.postservice.business.cases.GetPostsUseCase;
import com.example.postservice.postservice.business.dto.GetPostsRequest;
import com.example.postservice.postservice.business.dto.GetPostsResponse;
import com.example.postservice.postservice.domain.Post;
import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GetPostsUseCaseImpl implements GetPostsUseCase {

    private final PostRepository postRepository;

    @Override
    public GetPostsResponse getPosts(GetPostsRequest request) {

        List<PostEntity> results;

        if(request.getUserId() != null){
            results = postRepository.findAll();
        }
        else {
            results = new ArrayList<>();
        }
        final GetPostsResponse response = new GetPostsResponse();
        List<Post> posts = results
                .stream()
                .map(PostConverter::convert)
                .toList();
        response.setPosts(posts);

        return response;
    }
}
