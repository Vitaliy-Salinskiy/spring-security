package com.security.auth.auth;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.model.ProviderEnum;
import com.security.auth.service.AuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private AuthServiceImpl authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        String providerName = oauth2Token.getAuthorizedClientRegistrationId();

        OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest();
        oAuth2UserRequest.setProviderName(ProviderEnum.valueOf(providerName.toUpperCase()));

        if("github".equals(providerName)) {
            oAuth2UserRequest.setUsername((String) oAuth2User.getAttribute("login"));
            oAuth2UserRequest.setProviderId((String) Objects.requireNonNull(oAuth2User.getAttribute("id")).toString());
            oAuth2UserRequest.setImageUrl((String) oAuth2User.getAttribute("avatar_url"));
            if(oAuth2User.getAttribute("email") != null){
                oAuth2UserRequest.setEmail((String) oAuth2User.getAttribute("email"));
            }
        } else {
            oAuth2UserRequest.setUsername((String) oAuth2User.getAttribute("name"));
            oAuth2UserRequest.setProviderId((String) oAuth2User.getAttribute("sub"));
            oAuth2UserRequest.setImageUrl((String) oAuth2User.getAttribute("picture"));
            oAuth2UserRequest.setEmail((String) oAuth2User.getAttribute("email"));
        }

        authService.loginByProvider(oAuth2UserRequest);

        getRedirectStrategy().sendRedirect(request, response, "/api/users");

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
