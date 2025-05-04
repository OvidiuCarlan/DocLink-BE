package com.example.postservice.postservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String category;
}
