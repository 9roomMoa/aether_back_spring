package com.groommoa.aether_back_spring.global.auth.service;

import com.groommoa.aether_back_spring.domain.redis.entity.Token;
import com.groommoa.aether_back_spring.domain.redis.repository.TokenRepository;
import com.groommoa.aether_back_spring.global.auth.exception.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.TOKEN_EXPIRED;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public void deleteRefreshToken(String userKey) {
        tokenRepository.deleteById(userKey);
    }

    @Transactional
    public void saveOrUpdate(String userKey, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(userKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }

    @Transactional
    public void updateToken(String accessToken, Token token){
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
