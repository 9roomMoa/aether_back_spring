package com.groommoa.aether_back_spring.global.auth.service;

import com.groommoa.aether_back_spring.domain.user.entity.User;
import com.groommoa.aether_back_spring.domain.user.repository.UserRepository;
import com.groommoa.aether_back_spring.global.auth.dto.OAuth2UserInfo;
import com.groommoa.aether_back_spring.global.auth.model.PrincipalDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * OAuth2 인증 후 사용자 정보를 처리하는 서비스
 */
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 로그인 시 사용자 정보를 로드하고, 필요시 저장
     *
     * @param userRequest OAuth2 로그인 요청 정보
     * @return PrincipalDetails 객체 (Spring Security의 OAuth2User 구현체)
     * @throws OAuth2AuthenticationException OAuth2 인증 중 문제가 발생하면 예외 발생
     */
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2 제공자로부터 사용자 정보를 가져옴
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        // OAuth2 제공자(예: google, naver, kakao) 식별자
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 제공자의 유저 고유 식별 필드 (예: Google의 경우 "sub")
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserInfo 객체를 생성하여 제공자별로 사용자 정보를 매핑
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, attributes);

        // 기존 사용자가 있으면 가져오고, 없으면 저장
        User user = getOrSave(oAuth2UserInfo);

        // 인증된 사용자 정보를 PrincipalDetails 객체로 반환
        return new PrincipalDetails(user, attributes, userNameAttributeName);
    }

    /**
     * DB 에서 사용자를 조회하고, 존재하지 않으면 새 사용자로 저장
     *
     * @param oAuth2UserInfo OAuth2UserInfo DTO (OAuth2 제공자로부터 받은 사용자 정보)
     * @return 저장된 User 엔티티 객체
     */
    private User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        User user = userRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return userRepository.save(user);
    }
}
