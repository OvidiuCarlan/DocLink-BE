package com.example.doclink.business.impl;

import com.example.doclink.business.cases.CreateUserUseCase;
import com.example.doclink.business.dto.CreateUserRequest;
import com.example.doclink.business.dto.CreateUserResponse;
import com.example.doclink.persistance.RoleRepository;
import com.example.doclink.persistance.UserRepository;
import com.example.doclink.persistance.entity.RoleEnum;
import com.example.doclink.persistance.entity.UserEntity;
import com.example.doclink.persistance.entity.UserRoleEntity;

import com.example.doclink.service.WelcomeEmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final WelcomeEmailService welcomeEmailService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        UserEntity savedUser = savedNewUser(request);

        try {
            welcomeEmailService.sendWelcomeEmail(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getEmail(),
                    request.getRole()
            );
            System.out.println("Welcome email initiated for user: " + savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send welcome email for user " + savedUser.getEmail() + ": " + e.getMessage());
        }

        return CreateUserResponse.builder()
                .userId(savedUser.getId())
                .build();
    }

    private UserEntity savedNewUser(CreateUserRequest request){
        UserRoleEntity role = getUserRole(request.getRole());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(role)
                .build();

        return userRepository.save(newUser);
    }

    private UserRoleEntity getUserRole(String roleString) {
        RoleEnum roleEnum;

        try {
            roleEnum = (roleString != null && !roleString.isEmpty())
                    ? RoleEnum.valueOf(roleString.toUpperCase())
                    : RoleEnum.USER;
        } catch (IllegalArgumentException e) {
            roleEnum = RoleEnum.USER;
        }

        UserRoleEntity role = roleRepository.findByRole(roleEnum);

        if (role == null) {
            role = roleRepository.findByRole(RoleEnum.USER);
        }

        return role;
    }
}