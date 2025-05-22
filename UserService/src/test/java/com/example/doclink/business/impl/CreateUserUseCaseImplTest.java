package com.example.doclink.business.impl;

import com.example.doclink.business.dto.CreateUserRequest;
import com.example.doclink.business.dto.CreateUserResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCaseImpl createUserUseCase;

    private CreateUserRequest request;
    private UserRoleEntity userRole;
    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        request = CreateUserRequest.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        userRole = UserRoleEntity.builder()
                .id(1L)
                .role(RoleEnum.USER)
                .build();

        savedUser = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(userRole)
                .build();
    }

    @Test
    void createUser_WithoutRole_ShouldCreateUserWithDefaultUserRole() {
        // Arrange
        when(roleRepository.findByRole(RoleEnum.USER)).thenReturn(userRole);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act
        CreateUserResponse response = createUserUseCase.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());

        verify(roleRepository).findByRole(RoleEnum.USER);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_WithExistingRole_ShouldCreateUserWithProvidedRole() {
        // Arrange
        UserRoleEntity docRole = UserRoleEntity.builder()
                .id(2L)
                .role(RoleEnum.DOC)
                .build();
        request.setRole(docRole);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act
        CreateUserResponse response = createUserUseCase.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());

        verify(roleRepository, never()).findByRole(any());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_WhenDefaultRoleNotFound_ShouldCreateNewRole() {
        // Arrange
        when(roleRepository.findByRole(RoleEnum.USER)).thenReturn(null);
        when(roleRepository.save(any(UserRoleEntity.class))).thenReturn(userRole);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act
        CreateUserResponse response = createUserUseCase.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());

        verify(roleRepository).findByRole(RoleEnum.USER);
        verify(roleRepository).save(any(UserRoleEntity.class));
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }
}