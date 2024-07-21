package com.security.auth.service.impl;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.dto.SignupRequest;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;
import com.security.auth.model.User;
import com.security.auth.repository.UserRepository;
import com.security.auth.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findFirstByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    @Override
    public Optional<User> findFirstByUsername(String username) {
        return userRepository.findFirstByUsername(username);
    }

    @Override
    public Optional<User> findFirstByProviderId(String provideId) {
        return userRepository.findFirstByProviderId(provideId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndProviderIdIsNull(email);
    }

    @Override
    public void registerUserByCredentials(@NotNull SignupRequest signUpRequest) {
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role defaultRole = roleService.findFirstByName(RoleEnum.USER);

        user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));

        userRepository.save(user);
    }

    @Override
    public User registerOAuth2User(OAuth2UserRequest oAuth2UserRequest) {
        User user = new User();
        user.setProviderId(oAuth2UserRequest.getProviderId());
        user.setProviderName(oAuth2UserRequest.getProviderName());
        user.setImageUrl(oAuth2UserRequest.getImageUrl());
        user.setUsername(oAuth2UserRequest.getUsername());
        if(oAuth2UserRequest.getEmail() != null) user.setEmail(oAuth2UserRequest.getEmail());

        Role defaultRole = roleService.findFirstByName(RoleEnum.USER);

        user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));

        userRepository.save(user);

        return user;
    }

}
