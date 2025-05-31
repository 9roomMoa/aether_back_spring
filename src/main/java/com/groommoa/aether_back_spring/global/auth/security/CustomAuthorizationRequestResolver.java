package com.groommoa.aether_back_spring.global.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customizeState(defaultResolver.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customizeState(defaultResolver.resolve(request, request.getRequestURI().split("/")[3]), request);
    }

    private OAuth2AuthorizationRequest customizeState(OAuth2AuthorizationRequest originalRequest, HttpServletRequest request) {
        if (originalRequest == null) return null;

        String incomingState = request.getParameter("state");

        // 프론트엔드에서 전달한 state 덮어쓰기
        if (incomingState != null && !incomingState.isBlank()){
            return OAuth2AuthorizationRequest.from(originalRequest)
                    .state(incomingState)
                    .build();
        }

        return originalRequest;
    }
}
