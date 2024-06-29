package com.security.auth.service;

import com.security.auth.dto.OAuth2UserRequest;

public interface AuthService  {
    public Long loginByProvider(OAuth2UserRequest oAuth2UserRequest);
}
