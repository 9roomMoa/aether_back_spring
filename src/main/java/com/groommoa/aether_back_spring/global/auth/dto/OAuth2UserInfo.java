package com.groommoa.aether_back_spring.global.auth.dto;

import com.groommoa.aether_back_spring.domain.user.model.Role;
import com.groommoa.aether_back_spring.domain.user.model.User;
import com.groommoa.aether_back_spring.global.auth.exception.AuthException;
import lombok.Builder;

import java.util.Map;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.ILLEGAL_REGISTRATION_ID;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) throws AuthException {
        return switch (registrationId){
            case "google" -> ofGoogle(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .role(Role.USER)
                .build();
    }
}
