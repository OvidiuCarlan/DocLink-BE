package com.example.doclink.business.impl;

import com.example.doclink.business.dto.LoginRequest;
import com.example.doclink.business.dto.LoginResponse;
import com.example.doclink.business.exception.InvalidCredentialsException;
import com.example.doclink.configuration.security.token.AccessTokenEncoder;
import com.example.doclink.persistance.RoleRepository;
import com.example.doclink.persistance.UserRepository;
import com.example.doclink.persistance.entity.RoleEnum;
import com.example.doclink.persistance.entity.UserEntity;
import com.example.doclink.persistance.entity.UserRoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenEncoder accessTokenEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private LoginUseCaseImpl loginUseCase;

    private LoginRequest loginRequest;
    private UserEntity userEntity;
    private UserRoleEntity userRole;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        userRole = UserRoleEntity.builder()
                .id(1L)
                .role(RoleEnum.USER)
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(userRole)
                .build();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        when(userRepository.getUserEntityByEmail("john.doe@example.com")).thenReturn(userEntity);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(accessTokenEncoder.encode(any())).thenReturn("access-token-123");

        // Act
        LoginResponse response = loginUseCase.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token-123", response.getAccessToken());

        verify(userRepository).getUserEntityByEmail("john.doe@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(accessTokenEncoder).encode(any());
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowInvalidCredentialsException() {
        // Arrange
        when(userRepository.getUserEntityByEmail("john.doe@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> loginUseCase.login(loginRequest));

        verify(userRepository).getUserEntityByEmail("john.doe@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(accessTokenEncoder, never()).encode(any());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowInvalidCredentialsException() {
        // Arrange
        when(userRepository.getUserEntityByEmail("john.doe@example.com")).thenReturn(userEntity);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> loginUseCase.login(loginRequest));

        verify(userRepository).getUserEntityByEmail("john.doe@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(accessTokenEncoder, never()).encode(any());
    }

    @Test
    void login_WithUserWithoutRole_ShouldSetDefaultRoleAndLogin() {
        // Arrange
        userEntity.setRole(null);
        UserRoleEntity defaultRole = UserRoleEntity.builder()
                .id(1L)
                .role(RoleEnum.USER)
                .build();

        when(userRepository.getUserEntityByEmail("john.doe@example.com")).thenReturn(userEntity);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(roleRepository.findByRole(RoleEnum.USER)).thenReturn(defaultRole);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(accessTokenEncoder.encode(any())).thenReturn("access-token-123");

        // Act
        LoginResponse response = loginUseCase.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token-123", response.getAccessToken());

        verify(roleRepository).findByRole(RoleEnum.USER);
        verify(userRepository).save(any(UserEntity.class));
    }
}