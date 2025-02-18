package com.groommoa.aether_back_spring.global.auth.controller;

import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import com.groommoa.aether_back_spring.global.common.constants.HttpStatus;
import com.groommoa.aether_back_spring.global.common.constants.TokenKey;
import com.groommoa.aether_back_spring.global.common.response.CommonResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final TokenService tokenService;

    /**
     * 소셜 로그인 성공시 호출됨 (redirect)
     * @param accessToken 서버에서 발급된 JWT access token
     * @return 로그인 성공 응답과 JWT access token
     */
    @GetMapping("/success")
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
     * @param claims 현재 로그인한 사용자의 정보 (Spring Security 제공)
     * @return 로그아웃 성공 응답
     */
    @DeleteMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@AuthenticationPrincipal Claims claims) {

        // 사용자 계정과 연관된 refresh token 삭제
        String userId = claims.getSubject();
        tokenService.deleteRefreshToken(userId);

        // 로그아웃 성공 응답 객체 생성
        Map<String, Object> result = new HashMap<>();
        result.put("requestedId", userId);

        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "로그아웃에 성공했습니다.", result);
        return ResponseEntity.ok(response);
    }

    /**
     * accessToken을 재발급함
     * @param response HTTP 응답 객체
     * @return 재발급된 accessToken을 포함한 응답
     */
    @GetMapping("/reissue")
    public ResponseEntity<CommonResponse> reissueAccessToken(HttpServletResponse response) {
        String newAccessToken = response.getHeader(AUTHORIZATION);

        // 접두사 제거
        if (StringUtils.hasText(newAccessToken) && newAccessToken.startsWith(TokenKey.TOKEN_PREFIX)) {
            newAccessToken = newAccessToken.substring(TokenKey.TOKEN_PREFIX.length());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponse(HttpStatus.UNAUTHORIZED, "만료되지 않은 access token 입니다.", null));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);

        return ResponseEntity.ok(new CommonResponse(
                HttpStatus.OK, "access token 재발급에 성공했습니다.", result));
    }

    /**
     * 테스트용 엔드포인트
     *
     * @return 인증 테스트 성공 응답을 반환합니다.
     */
    @GetMapping("/test")
    public ResponseEntity<CommonResponse> test() {
        return ResponseEntity.ok(new CommonResponse(
                HttpStatus.OK, "auth 테스트 요청 입니다.", null));
    }
}