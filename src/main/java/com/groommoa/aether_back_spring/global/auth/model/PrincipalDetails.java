package com.groommoa.aether_back_spring.global.auth.model;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Spring Security에서 사용자의 인증 정보를 저장하는 객체
 * <P></P>
 * OAuth2 로그인 및 일반 로그인(UserDetails)을 모두 지원
 */
public record PrincipalDetails(
        Member member,                      // 인증된 사용자 객체
        Map<String, Object> attributes, // OAuth2 제공자로부터 받은 사용자 정보
        String attributeKey             // OAuth2 제공자의 사용자 식별 키
) implements OAuth2User, UserDetails {

    /**
     * OAuth2 제공자의 attributeKey를 사용하여 사용자의 고유 식별값 반환
     *
     * @return 사용자 식별값 (예: Google의 "sub" 값)
     */
    @Override
    public String getName() {
        return attributes.get(attributeKey).toString();
    }

    /**
     * OAuth2User에서 제공한 사용자 속성(attributes) 반환
     *
     * @return OAuth2 제공자로부터 받은 속성 정보
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 사용자의 권한(ROLE) 반환
     *
     * @return 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole().getKey())
        );
    }

    /**
     * OAuth2 로그인을 사용하는 경우 비밀번호가 필요하지 않으므로 null 반환
     *
     * @return null (비밀번호 사용 안 함)
     */
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getId();
    }
}
