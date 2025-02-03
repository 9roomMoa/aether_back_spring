package com.groommoa.aether_back_spring.global.auth.dto;

import com.groommoa.aether_back_spring.domain.user.model.Role;
import com.groommoa.aether_back_spring.domain.user.model.User;
import com.groommoa.aether_back_spring.global.auth.exception.AuthException;
import com.groommoa.aether_back_spring.global.common.utils.KeyGenerator;
import lombok.Builder;

import java.util.Map;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.ILLEGAL_REGISTRATION_ID;

/**
 * OAuth2 로그인 사용자 정보를 담는 DTO
 */
@Builder
public record OAuth2UserInfo(
        String name,    // 사용자 이름
        String email,   // 사용자 이메일
        String profile  // 프로필 이미지 URL
) {

    /**
     * OAuth2 제공자(registrationId)에 따라 적절한 사용자 정보를 매핑하는 정적 팩토리 메서드
     *
     * @param registrationId OAuth2 제공자 (예: "google")
     * @param attributes     제공자로부터 받은 사용자 정보 맵
     * @return OAuth2UserInfo 객체
     * @throws AuthException 지원하지 않는 제공자일 경우 예외 발생
     */
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) throws AuthException {
        return switch (registrationId){
            case "google" -> ofGoogle(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    /**
     * Google OAuth2 사용자 정보를 OAuth2UserInfo 객체로 변환
     *
     * @param attributes Google에서 받은 사용자 정보
     * @return 변환된 OAuth2UserInfo 객체
     */
    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    /**
     * OAuth2UserInfo 객체를 User 엔터티로 변환
     *
     * @return User 엔터티 객체
     */
    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .userKey(KeyGenerator.generateKey())
                .role(Role.USER)
                .build();
    }
}
