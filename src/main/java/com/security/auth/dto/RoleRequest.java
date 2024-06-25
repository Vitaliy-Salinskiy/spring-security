package com.security.auth.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    @NotBlank(message = "Role name is mandatory")
    private String roleName;
}
