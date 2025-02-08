package com.groommoa.aether_back_spring.global.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출
 * <p>
 * Spring Security에서 인증 예외(AuthenticationException)가 발생하면, 401 Unauthorized 응답 반환
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때 실행
     *
     * @param request       사용자의 HTTP 요청
     * @param response      HTTP 응답
     * @param authException 발생한 인증 예외 (AuthenticationException)
     * @throws IOException  응답을 클라이언트에 전달할 때 발생할 수 있는 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("AuthenticationException is occured", authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패하였습니다.");
    }
}
