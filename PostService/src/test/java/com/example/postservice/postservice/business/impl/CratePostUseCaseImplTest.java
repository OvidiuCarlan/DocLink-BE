package com.example.postservice.postservice.business.impl;

import com.example.postservice.postservice.business.dto.CreatePostRequest;
import com.example.postservice.postservice.business.dto.CreatePostResponse;
import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CratePostUseCaseImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CratePostUseCaseImpl createPostUseCase;

    private CreatePostRequest request;
    private PostEntity savedPost;

    @BeforeEach
    void setUp() {
        request = CreatePostRequest.builder()
                .id(1L)
                .userId(1L)
                .title("Test Post")
                .content("This is test content")
                .category("general")
                .build();

        savedPost = PostEntity.builder()
                .id("post-123")
                .userId("1")
                .title("Test Post")
                .content("This is test content")
                .category("general")
                .build();
    }

    @Test
    void createPost_WithValidRequest_ShouldReturnCreatePostResponse() {
        // Arrange
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedPost);

        // Act
        CreatePostResponse response = createPostUseCase.createPost(request);

        // Assert
        assertNotNull(response);
        assertEquals("post-123", response.getPostId());

        ArgumentCaptor<PostEntity> postCaptor = ArgumentCaptor.forClass(PostEntity.class);
        verify(postRepository).save(postCaptor.capture());

        PostEntity capturedPost = postCaptor.getValue();
        assertEquals("1", capturedPost.getUserId());
        assertEquals("Test Post", capturedPost.getTitle());
        assertEquals("This is test content", capturedPost.getContent());
        assertEquals("general", capturedPost.getCategory());
    }

    @Test
    void createPost_ShouldConvertUserIdToString() {
        // Arrange
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedPost);

        // Act
        createPostUseCase.createPost(request);

        // Assert
        ArgumentCaptor<PostEntity> postCaptor = ArgumentCaptor.forClass(PostEntity.class);
        verify(postRepository).save(postCaptor.capture());

        PostEntity capturedPost = postCaptor.getValue();
        assertEquals("1", capturedPost.getUserId());
    }
}