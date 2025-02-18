package com.groommoa.aether_back_spring.global.auth.security;

import com.groommoa.aether_back_spring.domain.redis.entity.Token;
import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.global.auth.exception.TokenException;
import com.groommoa.aether_back_spring.global.auth.model.PrincipalDetails;
import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.INVALID_JWT_SIGNATURE;
import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.INVALID_TOKEN;

/**
 * JWT 토큰을 생성하고 검증하는 제공자
 */
@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;
    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;   // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7; // 7일
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private final TokenService tokenService;

    /**
     * 애플리케이션이 시작될 때 JWT SecretKey 설정
     */
    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    /**
     * AccessToken 최초 생성
     *
     * @param principalDetails 인증된 유저 정보 객체
     * @return 생성된 AccessToken
     */
    public String generateAccessToken(PrincipalDetails principalDetails) {
        return generateToken(principalDetails, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * AccessToken 재발급
     *
     * @param claims 이전 jwt 토큰 claims
     * @return 생성된 AccessToken
     */
    public String generateAccessToken(Claims claims) {
        return generateToken(claims.getId(), claims, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * RefreshToken을 생성하고 Redis에 저장
     *
     * @param principalDetails 인증된 유저 정보 객체
     * @param accessToken    발급된 AccessToken
     */
    public void generateRefreshToken(PrincipalDetails principalDetails, String accessToken) {
        String refreshToken = generateToken(principalDetails, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveOrUpdate(principalDetails.getUsername(), refreshToken, accessToken);
    }

    /**
     * JWT 토큰을 생성하는 내부 메서드
     *
     * @param principalDetails 인증된 유저 정보 객체
     * @param expireTime     토큰 만료 시간
     * @return 생성된 JWT 토큰
     */
    private String generateToken(PrincipalDetails principalDetails, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        Member member = principalDetails.member();

        return Jwts.builder()
                .header().type("JWT")                   // 헤더에 타입 지정
                .and()
                .subject(member.getId())                // 멤버 id
                .claims(memberToMap(member))            // claim 정보
                .issuedAt(now)                          // 발급 시간
                .expiration(expiredDate)                // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS512)    // 서명 (HMAC SHA512)
                .compact();
    }

    /**
     * JWT 토큰 재생성
     *
     * @param id 사용자 고유 id
     * @param claims 이전 JWT 토큰 claims
     * @param expireTime 토큰 만료 시간
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String id, Claims claims, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .header().type("JWT")                   // 헤더에 타입 지정
                .and()
                .subject(id)                // 멤버 id
                .claims(claims)            // claim 정보
                .issuedAt(now)                          // 발급 시간
                .expiration(expiredDate)                // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS512)    // 서명 (HMAC SHA512)
                .compact();
    }

    private Map<String, Object> memberToMap(Member member){
        Map<String, Object> memberMap = new HashMap<>();

        memberMap.put(KEY_NAME, member.getName());
        memberMap.put(KEY_EMAIL, member.getEmail());
        memberMap.put(KEY_ROLE, member.getRole());

        return memberMap;
    }

    /**
     * JWT 토큰을 기반으로 Authentication 객체 생성
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        return new UsernamePasswordAuthenticationToken(claims, token, authorities);
    }

    /**
     * Claims에서 권한 정보를 추출하여 GrantedAuthority 리스트로 변환
     *
     * @param claims JWT의 클레임 정보
     * @return 권한 정보 리스트
     */
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    /**
     * AccessToken이 만료되었을 때 RefreshToken을 검증하여 새로운 AccessToken 재발급
     *
     * @param accessToken 기존 AccessToken
     * @return 새로 발급된 AccessToken (유효하지 않으면 null 반환)
     */
    public String reissueAccessToken(String accessToken){
        if (StringUtils.hasText(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            // RefreshToken이 유효한 경우 새로운 AccessToken 발급
            if (validateToken(refreshToken)){
                Claims claims = (Claims) getAuthentication(refreshToken).getPrincipal();
                String reissueAccessToken = generateAccessToken(claims);
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    /**
     * JWT 토큰 유효성 검사
     *
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());    // 현재 시간보다 만료 시간이 이후이면 유효
    }

    /**
     * JWT 토큰을 파싱하여 Claims 객체를 반환
     *
     * @param token 검증할 JWT 토큰
     * @return JWT Claims 객체
     * @throws TokenException 유효하지 않은 토큰일 경우 예외 발생
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e){
            // 만료된 토큰이라도 클레임 정보는 반환 가능
            return e.getClaims();
        } catch (MalformedJwtException e){
            // 잘못된 형식의 토큰 예외 처리
            throw new TokenException(INVALID_TOKEN);
        } catch (SecurityException e){
            // 서명 검증 실패 예외 처리
            throw new TokenException(INVALID_JWT_SIGNATURE);
        }
    }
}
