package com.example.doclink.persistance;

import com.example.doclink.persistance.entity.RoleEnum;
import com.example.doclink.persistance.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<UserRoleEntity, Long> {
    UserRoleEntity findByRole(RoleEnum roleEnum);
}
