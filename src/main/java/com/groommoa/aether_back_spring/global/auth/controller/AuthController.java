package com.groommoa.aether_back_spring.global.auth.controller;

import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import com.groommoa.aether_back_spring.global.common.constants.HttpStatus;
import com.groommoa.aether_back_spring.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final TokenService tokenService;

    /**
     * 소셜 로그인 성공시 호출됨 (redirect)
     * @param accessToken 서버에서 발급된 JWT access token
     * @return 로그인 성공 응답과 JWT access token
     */
    @GetMapping("/auth/success")
    public ResponseEntity<CommonResponse> loginSuccess(@RequestParam String accessToken) {
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);

        // 소셜 로그인 성공 응답 객체 생성
        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "소셜 로그인에 성공했습니다.", result);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     * @param userDetails 현재 로그인한 사용자의 정보 (Spring Security 제공)
     * @return 로그아웃 성공 응답
     */
    @DeleteMapping("/auth/logout")
    public ResponseEntity<CommonResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        // 사용자 계정과 연관된 refresh token 삭제
        tokenService.deleteRefreshToken(userDetails.getUsername());

        // 로그아웃 성공 응답 객체 생성
        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "로그아웃에 성공했습니다.", null);
        return ResponseEntity.ok(response);
    }
}
