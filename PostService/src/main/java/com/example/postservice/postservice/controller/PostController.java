package com.example.postservice.postservice.controller;

import com.example.postservice.postservice.business.cases.CreatePostUseCase;
import com.example.postservice.postservice.business.cases.GetPostsUseCase;
import com.example.postservice.postservice.business.dto.CreatePostRequest;
import com.example.postservice.postservice.business.dto.CreatePostResponse;
import com.example.postservice.postservice.business.dto.GetPostsRequest;
import com.example.postservice.postservice.business.dto.GetPostsResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@AllArgsConstructor
public class PostController {
    private final CreatePostUseCase createPostUseCase;
    private final GetPostsUseCase getPostsUseCase;

    @PostMapping()
    public ResponseEntity<CreatePostResponse> createPost(@RequestBody @Valid CreatePostRequest request){
        //long authenticatedUserId = authenticatedUser.getUserId();

//        if (request.getUserId() != authenticatedUserId) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        CreatePostResponse response = createPostUseCase.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(path = "{userId}")
    public ResponseEntity<GetPostsResponse> getPosts(
            @PathVariable(value = "userId") final String userId){

        GetPostsRequest request = GetPostsRequest.builder()
                .userId(userId)
                .build();

        GetPostsResponse response = getPostsUseCase.getPosts(request);
        return ResponseEntity.ok(response);
    }
}
