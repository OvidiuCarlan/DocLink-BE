//package com.example.postservice.postservice.controller;
//
//import com.example.postservice.postservice.business.cases.CreatePostUseCase;
//import com.example.postservice.postservice.business.cases.GetPostsUseCase;
//import com.example.postservice.postservice.business.dto.CreatePostRequest;
//import com.example.postservice.postservice.business.dto.CreatePostResponse;
//import com.example.postservice.postservice.business.dto.GetPostsResponse;
//import com.example.postservice.postservice.domain.Post;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(PostController.class)
//class PostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CreatePostUseCase createPostUseCase;
//
//    @MockBean
//    private GetPostsUseCase getPostsUseCase;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void createPost_WithValidRequest_ShouldReturnCreatedPost() throws Exception {
//        // Arrange
//        CreatePostRequest request = CreatePostRequest.builder()
//                .id(1L)
//                .userId(1L)
//                .title("Test Post")
//                .content("This is test content")
//                .category("general")
//                .build();
//
//        CreatePostResponse response = CreatePostResponse.builder()
//                .postId("post-123")
//                .build();
//
//        when(createPostUseCase.createPost(any(CreatePostRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(post("/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.postId").value("post-123"));
//    }
//
//    @Test
//    void getPosts_WithValidUserId_ShouldReturnPosts() throws Exception {
//        // Arrange
//        Post post = Post.builder()
//                .id("post-1")
//                .userId("1")
//                .title("Test Post")
//                .content("Test Content")
//                .category("general")
//                .build();
//
//        GetPostsResponse response = GetPostsResponse.builder()
//                .posts(Arrays.asList(post))
//                .build();
//
//        when(getPostsUseCase.getPosts(any())).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(get("/posts/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.posts").isArray())
//                .andExpect(jsonPath("$.posts[0].id").value("post-1"))
//                .andExpect(jsonPath("$.posts[0].title").value("Test Post"));
//    }
//}