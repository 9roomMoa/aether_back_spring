package com.groommoa.aether_back_spring.global.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Spring Security에서 인가되지 않은 사용자가
 * 보호된 리소스에 접근하려 할 때 처리하는 핸들러
 * <p>
 * AccessDeniedHandler를 구현하여 403 Forbidden 응답 반환
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 인가되지 않은 사용자가 보호된 리소스에 접근할 경우 호출
     *
     * @param request   사용자의 HTTP 요청
     * @param response  HTTP 응답
     * @param accessDeniedException 발생한 예외 (AccessDeniedException)
     * @throws IOException 응답을 클라이언트에 전달할 때 발생할 수 있는 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("AccessDeniedException is occured. ", accessDeniedException);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
    }
}
