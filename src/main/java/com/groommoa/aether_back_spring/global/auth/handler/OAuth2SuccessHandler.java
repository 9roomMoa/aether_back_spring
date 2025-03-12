package com.groommoa.aether_back_spring.global.auth.handler;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.global.auth.model.PrincipalDetails;
import com.groommoa.aether_back_spring.global.auth.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 인증 성공 후 실행되는 핸들러
 * <P></P>
 * 인증된 사용자에게 AccessToken과 RefreshToken을 발급하고, 지정된 URI로 리다이렉트
 */
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Value("${login.success.base-url}")
    private String baseUrl;

    /**
     * OAuth2 로그인 성공 시 실행되는 메서드
     *
     * @param request        사용자의 HTTP 요청
     * @param response       HTTP 응답
     * @param authentication 인증 정보 (사용자 정보 포함)
     * @throws IOException   응답을 클라이언트에 전달할 때 발생할 수 있는 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // baseUrl이 정상적으로 로드되었는지 확인
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("Base URL이 properties에서 로드되지 않았습니다.");
        }

        // accessToken, refreshToken 발급
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(principalDetails);
        tokenProvider.generateRefreshToken(principalDetails, accessToken);

        // 사용자 정보 추출
        Member member = principalDetails.member();

        HttpSession session = request.getSession();
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("member", member);

        // 클라이언트를 인증 성공 페이지로 리다이렉트
        String redirectUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/auth/success")
                .queryParam("id", member.getId())
                .queryParam("accessToken", accessToken)
                .queryParam("username", URLEncoder.encode(member.getName(), StandardCharsets.UTF_8))
                .queryParam("email", member.getEmail())
                .build().toUriString();

        response.sendRedirect(redirectUrl);

    }
}
