//package com.example.doclink.controller;
//
//import com.example.doclink.business.cases.CreateUserUseCase;
//import com.example.doclink.business.cases.LoginUseCase;
//import com.example.doclink.business.dto.CreateUserRequest;
//import com.example.doclink.business.dto.CreateUserResponse;
//import com.example.doclink.business.dto.LoginRequest;
//import com.example.doclink.business.dto.LoginResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UserController.class)
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CreateUserUseCase createUserUseCase;
//
//    @MockBean
//    private LoginUseCase loginUseCase;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void createUser_WithValidRequest_ShouldReturnCreatedUser() throws Exception {
//        // Arrange
//        CreateUserRequest request = CreateUserRequest.builder()
//                .id(1L)
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@example.com")
//                .password("password123")
//                .build();
//
//        CreateUserResponse response = CreateUserResponse.builder()
//                .userId(1L)
//                .build();
//
//        when(createUserUseCase.createUser(any(CreateUserRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.userId").value(1L));
//    }
//
//    @Test
//    void login_WithValidCredentials_ShouldReturnAccessToken() throws Exception {
//        // Arrange
//        LoginRequest request = LoginRequest.builder()
//                .email("john.doe@example.com")
//                .password("password123")
//                .build();
//
//        LoginResponse response = LoginResponse.builder()
//                .accessToken("access-token-123")
//                .build();
//
//        when(loginUseCase.login(any(LoginRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(post("/users/tokens")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.accessToken").value("access-token-123"));
//    }
//}