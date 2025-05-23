package com.groommoa.aether_back_spring.global.auth.dto;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {

    private final String username;
    private final String rank;
    private final String userId;
    private final String email;

    public static UserProfileResponseDto from(Member member) {
        return UserProfileResponseDto.builder()
                .username(member.getName())
                .rank(String.valueOf(member.getRank()))
                .userId(member.getId())
                .email(member.getEmail())
                .build();
    }

}
