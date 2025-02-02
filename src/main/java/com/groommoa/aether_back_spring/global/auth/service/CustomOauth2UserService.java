package com.groommoa.aether_back_spring.global.auth.service;

import com.groommoa.aether_back_spring.domain.user.model.User;
import com.groommoa.aether_back_spring.domain.user.model.UserRepository;
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

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2 정보를 가져옵니다.
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        // 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, attributes);

        // 회원가입 및 로그인
        User user = getOrSave(oAuth2UserInfo);

        // OAuth2User로 반환
        return new PrincipalDetails(user, attributes, userNameAttributeName);
    }

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        User user = userRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return userRepository.save(user);
    }
}
