package com.security.auth.dto;

import com.security.auth.model.RoleEnum;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    @NotBlank(message = "Role name is mandatory")
    private RoleEnum roleName;
}
