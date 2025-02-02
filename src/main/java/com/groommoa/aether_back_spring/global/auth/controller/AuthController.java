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

    @GetMapping("/auth/success")
    public ResponseEntity<CommonResponse> loginSuccess(@RequestParam String accessToken) {
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);

        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "소셜 로그인에 성공했습니다.", result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<CommonResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        tokenService.deleteRefreshToken(userDetails.getUsername());

        CommonResponse response = new CommonResponse(
                HttpStatus.OK, "로그아웃에 성공했습니다.", null);
        return ResponseEntity.ok(response);
    }
}
