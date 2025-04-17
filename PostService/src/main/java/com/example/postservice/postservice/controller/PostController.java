package com.example.postservice.postservice.controller;

import com.example.postservice.postservice.business.cases.CreatePostUseCase;
import com.example.postservice.postservice.business.dto.CreatePostRequest;
import com.example.postservice.postservice.business.dto.CreatePostResponse;
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

    @PostMapping()
    public ResponseEntity<CreatePostResponse> createPost(@RequestBody @Valid CreatePostRequest request){
        //long authenticatedUserId = authenticatedUser.getUserId();

//        if (request.getUserId() != authenticatedUserId) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        CreatePostResponse response = createPostUseCase.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
