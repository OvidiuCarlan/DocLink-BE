package com.example.postservice.postservice.business.dto;

import com.example.postservice.postservice.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPostsResponse {
    private List<Post> posts;
}
