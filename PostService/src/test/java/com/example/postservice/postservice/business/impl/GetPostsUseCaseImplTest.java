package com.example.postservice.postservice.business.impl;

import com.example.postservice.postservice.business.dto.GetPostsRequest;
import com.example.postservice.postservice.business.dto.GetPostsResponse;
import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPostsUseCaseImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private GetPostsUseCaseImpl getPostsUseCase;

    private List<PostEntity> postEntities;

    @BeforeEach
    void setUp() {
        postEntities = Arrays.asList(
                PostEntity.builder()
                        .id("post-1")
                        .userId("1")
                        .title("First Post")
                        .content("Content of first post")
                        .category("general")
                        .build(),
                PostEntity.builder()
                        .id("post-2")
                        .userId("1")
                        .title("Second Post")
                        .content("Content of second post")
                        .category("health-tip")
                        .build()
        );
    }

    @Test
    void getPosts_WithValidUserId_ShouldReturnAllPosts() {
        // Arrange
        GetPostsRequest request = GetPostsRequest.builder()
                .userId("1")
                .build();

        when(postRepository.findAll()).thenReturn(postEntities);

        // Act
        GetPostsResponse response = getPostsUseCase.getPosts(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());

        verify(postRepository).findAll();
    }

    @Test
    void getPosts_WithNullUserId_ShouldReturnEmptyResponse() {
        // Arrange
        GetPostsRequest request = GetPostsRequest.builder()
                .userId(null)
                .build();

        // Act
        GetPostsResponse response = getPostsUseCase.getPosts(request);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getPosts().size());

        verify(postRepository, never()).findAll();
    }

    @Test
    void getPosts_ShouldConvertEntitiesToDomainObjects() {
        // Arrange
        GetPostsRequest request = GetPostsRequest.builder()
                .userId("1")
                .build();

        when(postRepository.findAll()).thenReturn(postEntities);

        // Act
        GetPostsResponse response = getPostsUseCase.getPosts(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());

        assertEquals("post-1", response.getPosts().get(0).getId());
        assertEquals("1", response.getPosts().get(0).getUserId());
        assertEquals("First Post", response.getPosts().get(0).getTitle());
        assertEquals("Content of first post", response.getPosts().get(0).getContent());
        assertEquals("general", response.getPosts().get(0).getCategory());
    }
}