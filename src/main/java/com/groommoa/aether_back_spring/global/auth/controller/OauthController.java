package com.groommoa.aether_back_spring.global.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.groommoa.aether_back_spring.global.auth.service.OauthService;
import com.groommoa.aether_back_spring.global.common.BaseResponse;
import com.groommoa.aether_back_spring.global.common.SocialLoginType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller 생성
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class OauthController {
    private final OauthService oauthService;

    /**
     * 사용자로부터 Social Login 요청을 처리
     * @param socialLoginType (GOOGLE, NAVER, KAKAO)
     */
    @GetMapping(value={"/{socialLoginType}"})
    public void requestSocialLogin(@PathVariable SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        oauthService.requestSocialLogin(socialLoginType);
    }

    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     * @param socialLoginType (GOOGLE, NAVER, KAKAO)
     * @param code API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 데이터
     */
    @GetMapping(value = "/{socialLoginType}/callback")
    public ResponseEntity<BaseResponse> callback(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 Access Code :: {}", code);
        return oauthService.getAccessToken(socialLoginType, code);
    }

    /**
     * Google 사용자 프로필 정보 요청
     * @param socialLoginType (GOOGLE, NAVER, KAKAO)
     * @return 사용자 프로필 정보를 담은 Json 데이터
     */
    @GetMapping(value = "/{socialLoginType}/profile")
    public ResponseEntity<BaseResponse> getUserInfo(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestHeader(name = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        log.info(">> {} 사용자 프로필 요청 :: Access Token: {}", socialLoginType, accessToken);

        return oauthService.getUserInfo(socialLoginType, accessToken);
    }

    /**
     * 로그아웃
     */
    @PostMapping(value = "/logout")
    public void requestLogout(){

    }
}