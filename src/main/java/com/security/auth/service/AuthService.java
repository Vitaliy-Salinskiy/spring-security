package com.security.auth.service;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.dto.SignupRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService  {
    Long loginByProvider(HttpServletResponse response, OAuth2UserRequest oAuth2UserRequest);
    void registerUserByCredentials(SignupRequest signUpRequest);
    void  refreshTokens(HttpServletResponse response, String refreshToken);
}
