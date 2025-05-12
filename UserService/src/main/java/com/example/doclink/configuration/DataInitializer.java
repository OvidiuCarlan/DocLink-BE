package com.example.doclink.configuration;

import com.example.doclink.persistance.RoleRepository;
import com.example.doclink.persistance.entity.RoleEnum;
import com.example.doclink.persistance.entity.UserRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByRole(RoleEnum.USER) == null) {
            roleRepository.save(UserRoleEntity.builder().role(RoleEnum.USER).build());
        }

        if (roleRepository.findByRole(RoleEnum.DOC) == null) {
            roleRepository.save(UserRoleEntity.builder().role(RoleEnum.DOC).build());
        }

        if (roleRepository.findByRole(RoleEnum.ADMIN) == null) {
            roleRepository.save(UserRoleEntity.builder().role(RoleEnum.ADMIN).build());
        }
    }
}