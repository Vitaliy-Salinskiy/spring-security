package com.security.auth.service;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.dto.SignupRequest;
import com.security.auth.model.User;
import java.util.Optional;

public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void registerUserByCredentials(SignupRequest signUpRequest);
    User registerOAuth2User(OAuth2UserRequest oAuth2UserRequest);
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByUsername(String username);
    Optional<User> findFirstById(Long id);
    Optional<User> findFirstByProviderId(String provideId);
}
