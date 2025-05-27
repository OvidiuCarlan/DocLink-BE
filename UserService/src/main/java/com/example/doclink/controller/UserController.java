package com.example.doclink.controller;

import com.example.doclink.business.cases.CreateUserUseCase;
import com.example.doclink.business.cases.DeleteUserUseCase;
import com.example.doclink.business.cases.GetUserUseCase;
import com.example.doclink.business.dto.*;
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
    private final GetUserUseCase getUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;



    @Autowired
    private final LoginUseCase loginUseCase;

    @PostMapping()
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        CreateUserResponse response = createUserUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long userId) {
        GetUserResponse response = getUserUseCase.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping(path = "/tokens")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse loginResponse = loginUseCase.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        deleteUserUseCase.initiateUserDeletion(userId);
        return ResponseEntity.accepted().build(); // 202 - Deletion initiated
    }
}
