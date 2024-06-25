package com.security.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest extends LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 25, message = "Username should be between 4 and 25 characters")
    private String username;
}
