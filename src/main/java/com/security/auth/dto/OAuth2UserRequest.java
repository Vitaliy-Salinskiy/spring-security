package com.security.auth.dto;

import com.security.auth.model.ProviderEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String username;

    @Nullable
    @Email(message = "Email should be valid")
    private String email;

    @URL(message = "Image url should be valid")
    @NotBlank(message = "Image url is mandatory")
    private String imageUrl;

    @NotBlank(message = "Provider id is mandatory")
    private String  providerId;

    @NotBlank(message = "Provider name is mandatory")
    private ProviderEnum providerName;
}
