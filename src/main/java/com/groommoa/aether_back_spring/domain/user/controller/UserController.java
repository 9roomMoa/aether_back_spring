package com.groommoa.aether_back_spring.domain.user.controller;

import com.groommoa.aether_back_spring.domain.user.service.UserService;
import com.groommoa.aether_back_spring.global.common.constants.HttpStatus;
import com.groommoa.aether_back_spring.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    /**
     * 현재 인증된 사용자의 정보 조회
     * @param userDetails 인증된 사용자의 정보를 포함하는 객체 (Spring Security 제공)
     * @return 사용자 정보
     */
    @GetMapping
    public ResponseEntity<CommonResponse> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails){
        Map<String, Object> result = userService.getUserInfo(userDetails.getUsername());
        CommonResponse response = new CommonResponse(
                HttpStatus.OK,
                "유저 정보 조회에 성공했습니다.",
                result);
        return ResponseEntity.ok(response);
    }
}
