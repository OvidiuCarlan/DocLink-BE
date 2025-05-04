package com.example.postservice.postservice.business.impl;

import com.example.postservice.postservice.domain.Post;
import com.example.postservice.postservice.persistance.entity.PostEntity;

public class PostConverter {

    public static Post convert(PostEntity post){
        return Post.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .build();
    }
}
