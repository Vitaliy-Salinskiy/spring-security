package com.security.auth.auth;

import com.security.auth.dto.OAuth2UserRequest;
import com.security.auth.model.GitHubEmail;
import com.security.auth.model.ProviderEnum;
import com.security.auth.service.impl.AuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private AuthServiceImpl authService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

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
            } else {
                getGithubUserEmail(oauth2Token, oAuth2UserRequest);
            }
        } else {
            oAuth2UserRequest.setUsername((String) oAuth2User.getAttribute("name"));
            oAuth2UserRequest.setProviderId((String) oAuth2User.getAttribute("sub"));
            oAuth2UserRequest.setImageUrl((String) oAuth2User.getAttribute("picture"));
            oAuth2UserRequest.setEmail((String) oAuth2User.getAttribute("email"));
        }

        Long userId = authService.loginByProvider(response, oAuth2UserRequest);

        if(userId != null) {
          getRedirectStrategy().sendRedirect(request, response, "/api/users/" + userId);
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/api/users");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    void getGithubUserEmail(@NotNull OAuth2AuthenticationToken oauth2Token, OAuth2UserRequest oAuth2UserRequest) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauth2Token.getAuthorizedClientRegistrationId(),
                oauth2Token.getName()
        );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GitHubEmail[]> githubResponse = restTemplate.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, GitHubEmail[].class);

        GitHubEmail[] emails = githubResponse.getBody();
        if(emails != null && emails[0].getEmail() != null) {
            oAuth2UserRequest.setEmail(emails[0].getEmail());
        }
    }

}
