package com.groommoa.aether_back_spring.global.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.groommoa.aether_back_spring.global.auth.service.impl.GoogleOauth;
import com.groommoa.aether_back_spring.global.auth.service.impl.KakaoOauth;
import com.groommoa.aether_back_spring.global.auth.service.impl.NaverOauth;
import com.groommoa.aether_back_spring.global.common.BaseResponse;
import com.groommoa.aether_back_spring.global.common.SocialLoginType;
import org.springframework.http.ResponseEntity;

// 소셜 로그인 타입별로 공통적으로 사용될 interface 생성
public interface SocialOauth {
    /**
     * 각 Social Login 페이지로 Redirect 처리할 URL Build
     * 사용자로부터 로그인 요청을 받아 Social Login Server 인증용 code 요청
     */
    String getOauthRedirectURL();

    /**
     * API Server 로부터 받은 code를 활용하여 Access Token 요청
     * @param code API Server 에서 받아온 code
     * @return API Server 로부터 응답받은 Json 형태의 결과 중 access_token
     */
    ResponseEntity<BaseResponse> getAccessToken(String code);

    /**
     * API Server 로부터 받은 Access Token을 활용하여 유저 프로필 요청
     * @param accessToken API Server에서 받아온 Access Token
     * @return API 서버로부터 응답 받은 Json 결과
     */
    ResponseEntity<BaseResponse> getUserInfo(String accessToken);

    default SocialLoginType type() {
        if (this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof NaverOauth) {
            return SocialLoginType.NAVER;
        } else if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }
}
