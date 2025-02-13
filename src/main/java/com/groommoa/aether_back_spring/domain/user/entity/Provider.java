package com.groommoa.aether_back_spring.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    Google("PROVIDER_GOOGLE"),
    Kakao("PROVIDER_KAKAO"),
    Naver("PROVIDER_NAVER");

    private final String key;
}
