package com.security.auth.service;

import com.security.auth.exception.CustomException;
import com.security.auth.repository.RoleRepository;
import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;
import com.security.auth.model.User;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Long loginByProvider(@Valid OAuth2UserRequest oAuth2UserRequest) {

       Optional<User> candidate = userRepository.findByProviderId(oAuth2UserRequest.getProviderId());

        if(candidate.isEmpty()){
            User user = new User();
            user.setProviderId(oAuth2UserRequest.getProviderId());
            user.setProviderName(oAuth2UserRequest.getProviderName());
            user.setImageUrl(oAuth2UserRequest.getImageUrl());
            user.setUsername(oAuth2UserRequest.getUsername());
            if(oAuth2UserRequest.getEmail() != null) user.setEmail(oAuth2UserRequest.getEmail());

            Role defaultRole = roleRepository.findByName(RoleEnum.USER)
                    .orElseThrow(() -> new CustomException("Error: Role is not found.", HttpStatus.NOT_FOUND));

            user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));

            userRepository.save(user);
            return user.getId();
        }

        return candidate.get().getId();
    }
}
