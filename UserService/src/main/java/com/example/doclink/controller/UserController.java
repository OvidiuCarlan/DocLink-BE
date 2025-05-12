package com.example.doclink.controller;

import com.example.doclink.business.cases.CreateUserUseCase;
import com.example.doclink.business.dto.CreateUserRequest;
import com.example.doclink.business.dto.CreateUserResponse;
import com.example.doclink.business.dto.LoginRequest;
import com.example.doclink.business.dto.LoginResponse;
import com.example.doclink.configuration.security.token.AccessToken;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.doclink.business.cases.LoginUseCase;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @Autowired
    private final LoginUseCase loginUseCase;

    @PostMapping()
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        CreateUserResponse response = createUserUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping(path = "/tokens")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse loginResponse = loginUseCase.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }
}
