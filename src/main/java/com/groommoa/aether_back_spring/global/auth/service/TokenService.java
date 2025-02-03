package com.groommoa.aether_back_spring.global.auth.service;

import com.groommoa.aether_back_spring.domain.redis.entity.Token;
import com.groommoa.aether_back_spring.domain.redis.repository.TokenRepository;
import com.groommoa.aether_back_spring.global.auth.exception.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.TOKEN_EXPIRED;

/**
 * 액세스 토큰 및 리프레시 토큰을 관리하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    /**
     * 특정 사용자의 리프레시 토큰을 삭제합니다.
     *
     * @param userKey 사용자 고유 키
     */
    public void deleteRefreshToken(String userKey) {
        tokenRepository.deleteById(userKey);
    }

    /**
     * 리프레시 토큰과 액세스 토큰을 저장하거나 업데이트합니다.
     *
     * @param userKey      사용자 고유 키
     * @param refreshToken 새 리프레시 토큰
     * @param accessToken  새로운 액세스 토큰
     */
    @Transactional
    public void saveOrUpdate(String userKey, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(userKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    /**
     * 액세스 토큰을 기반으로 토큰을 찾고, 없으면 예외 생성
     *
     * @param accessToken 액세스 토큰
     * @return 찾은 Token 객체
     * @throws TokenException 토큰이 만료되었거나 존재하지 않을 경우 발생
     */
    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }

    /**
     * 기존 토큰의 액세스 토큰을 Redis에 업데이트
     *
     * @param accessToken 새로 갱신된 액세스 토큰
     * @param token       업데이트할 대상 Token 객체
     */
    @Transactional
    public void updateToken(String accessToken, Token token){
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
