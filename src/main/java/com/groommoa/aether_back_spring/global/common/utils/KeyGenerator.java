package com.groommoa.aether_back_spring.global.common.utils;

import java.util.UUID;

/**
 * 고유 키(문자열)를 생성하는 유틸리티
 */
public final class KeyGenerator {

    /**
     * 고유 키 생성
     * @return 랜덤으로 생성된 32자리 문자열 (하이픈 제거된 UUID)
     */
    public static String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
