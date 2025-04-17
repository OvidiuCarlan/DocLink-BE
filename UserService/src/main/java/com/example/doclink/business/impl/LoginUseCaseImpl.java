package com.example.doclink.business.impl;


import com.example.doclink.business.cases.LoginUseCase;
import com.example.doclink.business.dto.LoginRequest;
import com.example.doclink.business.dto.LoginResponse;
import com.example.doclink.business.exception.InvalidCredentialsException;
import com.example.doclink.configuration.security.token.AccessTokenEncoder;
import com.example.doclink.configuration.security.token.impl.AccessTokenImpl;
import com.example.doclink.persistance.UserRepository;
import com.example.doclink.persistance.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenEncoder accessTokenEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.getUserEntityByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        if (!matchesPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = generateAccessToken(user);
        return LoginResponse.builder().accessToken(accessToken).build();
    }

    private boolean matchesPassword(String rawPassword, String encodedPassword) {

         return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private String generateAccessToken(UserEntity user) {

        return accessTokenEncoder.encode(
                new AccessTokenImpl(user.getEmail(), user.getId(), List.of(user.getRole().getRole().toString())));
    }
}
