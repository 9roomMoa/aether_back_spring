package com.groommoa.aether_back_spring.global.auth.security;

import com.groommoa.aether_back_spring.global.auth.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 관련 예외(TokenException)가 발생할 경우
 * 이를 처리하는 Spring Security 필터
 */
@Getter
public class TokenExceptionFilter extends OncePerRequestFilter {

    /**
     * 요청이 들어올 때마다 실행되는 필터 메서드
     * <P></P>
     * 필터 체인을 실행하고, TokenException이 발생하면 적절한 HTTP 응답을 반환
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 실행 중 예외 발생 시 처리
     * @throws IOException      입출력 예외 발생 시 처리
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e){
            // TokenException 발생 시, 해당 예외의 HTTP 상태 코드와 메시지를 클라이언트에 반환
            response.sendError(e.getErrorCode().getHttpStatus().value(), e.getMessage());
        }
    }
}
