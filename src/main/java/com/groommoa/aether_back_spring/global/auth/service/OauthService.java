package com.groommoa.aether_back_spring.global.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.groommoa.aether_back_spring.global.common.BaseResponse;
import com.groommoa.aether_back_spring.global.common.SocialLoginType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * OauthService - 소셜 로그인 요청 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class OauthService {
    private final List<SocialOauth> socialOauthList; // 모든 소셜 로그인 구현체 주입
    private final HttpServletResponse response;

    /**
     * 소셜 로그인 요청을 처리하여 리다이렉트
     *
     * @param socialLoginType 소셜 로그인 타입 (GOOGLE, NAVER, KAKAO)
     */
    public void requestSocialLogin(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 소셜 로그인 Access Token 가져오기
     *
     * @param socialLoginType 소셜 로그인 타입
     * @param code 소셜 로그인 API 서버에서 전달받은 인증 코드
     * @return Access Token
     */
    public ResponseEntity<BaseResponse> getAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getAccessToken(code);
    }

    /**
     * 사용자 프로필 정보 가져오기
     *
     * @param socialLoginType 소셜 로그인 타입
     * @param accessToken 사용자 인증 토큰
     * @return 사용자 프로필 정보 (JsonNode 형태)
     */
    public ResponseEntity<BaseResponse> getUserInfo(SocialLoginType socialLoginType, String accessToken) {
        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        return socialOauth.getUserInfo(accessToken);
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }
}
