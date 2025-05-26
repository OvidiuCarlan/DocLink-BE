package com.example.doclink.business.dto;

import com.example.doclink.persistance.entity.RoleEnum;
import com.example.doclink.persistance.entity.UserRoleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotNull
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private UserRoleEntity role;

    @JsonProperty("role")
    public void setRoleFromString(String roleString) {
        if (roleString != null && !roleString.trim().isEmpty()) {
            try {
                RoleEnum roleEnum = RoleEnum.valueOf(roleString.toUpperCase());
                this.role = UserRoleEntity.builder().role(roleEnum).build();
            } catch (IllegalArgumentException e) {
                this.role = UserRoleEntity.builder().role(RoleEnum.USER).build();
            }
        }
    }
}
