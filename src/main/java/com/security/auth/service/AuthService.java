package com.security.auth.service;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.dto.SignupRequest;

public interface AuthService  {
    Long loginByProvider(OAuth2UserRequest oAuth2UserRequest);

    void registerUserByCredentials(SignupRequest signUpRequest);
}
