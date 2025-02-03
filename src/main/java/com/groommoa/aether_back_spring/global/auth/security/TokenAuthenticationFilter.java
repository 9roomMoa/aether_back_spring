package com.groommoa.aether_back_spring.global.auth.security;

import com.groommoa.aether_back_spring.global.common.constants.TokenKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * JWT 기반 인증 필터
 * <P></P>
 * 매 요청마다 실행되며, 요청 헤더에서 토큰을 추출하고 인증을 수행
 */
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    /**
     * 요청이 들어올 때마다 실행되는 필터 메서드.
     * <P></P>
     * JWT를 검증하고, 유효하면 인증 설정
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
        // HTTP 요청 헤더에서 AccessToken 을 가져옴
        String accessToken = resolveToken(request);

        // accessToken 검증
        if (tokenProvider.validateToken(accessToken)) {
            // 토큰이 유효한 경우 인증 설정
            setAuthentication(accessToken);
        } else {
            // 토큰이 만료된 경우 새로운 AccessToken 재발급
            String reissueAccessToken = tokenProvider.reissueAccessToken(accessToken);

            if (StringUtils.hasText(reissueAccessToken)) {
                setAuthentication(reissueAccessToken);

                // 클라이언트에 재발급된 AccessToken을 응답 헤더에 추가
                response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + reissueAccessToken);
            }
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    /**
     * 토큰을 검증한 후 SecurityContext에 인증 정보를 저장
     *
     * @param accessToken 유효한 JWT 액세스 토큰
     */
    private void setAuthentication(String accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 요청 헤더에서 JWT 토큰 추출.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 토큰 (Bearer 제거 후)
     */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(token) || !token.startsWith(TokenKey.TOKEN_PREFIX)) {
            return null;
        }
        return token.substring(TokenKey.TOKEN_PREFIX.length());
    }
}
