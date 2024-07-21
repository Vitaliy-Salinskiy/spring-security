package com.security.auth.service.impl;

import com.security.auth.auth.JwtTokenProvider;
import com.security.auth.dto.SignupRequest;
import com.security.auth.exception.CustomException;
import com.security.auth.model.User;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public Long loginByProvider(@NotNull HttpServletResponse response, @Valid OAuth2UserRequest oAuth2UserRequest) {

       Optional<User> candidate = userService.findFirstByProviderId(oAuth2UserRequest.getProviderId());
        User user;

        user = candidate.orElseGet(() -> userService.registerOAuth2User(oAuth2UserRequest));

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        jwtTokenProvider.setCookies(response, accessToken, refreshToken); ;

        return user.getId();
    }

    @Override
    public void registerUserByCredentials(SignupRequest signUpRequest) {
        if(userService.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomException("Error: Email: " + signUpRequest.getEmail()  + " is already in use!", HttpStatus.CONFLICT);
        }

        userService.registerUserByCredentials(signUpRequest);
    }

}
