package com.groommoa.aether_back_spring.global.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {

    @Override
    public SocialLoginType convert(String source) {
        try {
            return SocialLoginType.valueOf(source.toUpperCase()); // 대소문자 무시하고 매칭
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 SocialLoginType입니다: " + source);
        }
    }
}
