package com.groommoa.aether_back_spring.global.auth.controller;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.global.auth.dto.UpdateUserProfileRequestDto;
import com.groommoa.aether_back_spring.global.auth.dto.UpdateUserProfileResponseDto;
import com.groommoa.aether_back_spring.global.auth.service.AuthService;
import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import com.groommoa.aether_back_spring.global.common.constants.HttpStatus;
import com.groommoa.aether_back_spring.global.common.constants.TokenKey;
import com.groommoa.aether_back_spring.global.common.response.CommonResponse;
import com.groommoa.aether_back_spring.global.common.utils.DtoUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    /**
     * 소셜 로그인 성공시 호출됨 (redirect)
     * @param session 서버에서 생성된 데이터 전달용 사용자 세션
     * @return 로그인 성공 응답과 JWT access token
     */
    @GetMapping("/success")
    public ResponseEntity<Void> loginSuccess(HttpSession session) throws IOException {
        // 세션에서 데이터 읽기
        String accessToken = (String) session.getAttribute("accessToken");
        Member member = (Member) session.getAttribute("member");

        // 프론트엔드 엔드포인트로 리다이렉트
        String baseFrontendUrl = "http://localhost:5173";
        String encodedUsername = Base64.getEncoder().encodeToString(member.getName().getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        String redirectUrl = UriComponentsBuilder.fromUriString(baseFrontendUrl)
                .path("sign-up")
                .queryParam("id", member.getId())
                .queryParam("accessToken", accessToken)
                .queryParam("username", member.getName())
                .queryParam("email", member.getEmail())
                .toUriString();
        headers.setLocation(URI.create(redirectUrl));

        // 쿠키로 사용자 데이터 전달
        headers.add(HttpHeaders.SET_COOKIE, "accessToken=" + accessToken + "; Secure; SameSite=None; Path=/; Max-Age=3600");
        headers.add(HttpHeaders.SET_COOKIE, "id=" + member.getId() + "; Secure; SameSite=None; Path=/; Max-Age=3600");
        headers.add(HttpHeaders.SET_COOKIE, "username=" + encodedUsername + "; Secure; SameSite=None; Path=/; Max-Age=3600");
        headers.add(HttpHeaders.SET_COOKIE, "email=" + member.getEmail() + "; Secure; SameSite=None; Path=/; Max-Age=3600");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .headers(headers)
                .body(null);
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

    @PatchMapping("/profile")
    public ResponseEntity<CommonResponse> updateUserProfile(@RequestBody @Valid UpdateUserProfileRequestDto request,
                                                            @AuthenticationPrincipal Claims claims){
        Member updatedMember = authService.updateUserProfile(claims.getSubject(), request);
        UpdateUserProfileResponseDto responseDto = new UpdateUserProfileResponseDto(updatedMember);

        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "유저 프로필 정보 수정에 성공했습니다.", DtoUtils.toMap(responseDto));
        return ResponseEntity.ok(response);
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