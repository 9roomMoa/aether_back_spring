package com.groommoa.aether_back_spring.global.auth.dto;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.domain.user.entity.Rank;
import com.groommoa.aether_back_spring.domain.user.entity.Role;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UpdateUserProfileResponseDto {

    private final String name;
    private final Role role;
    private final Rank rank;
    private final Instant createdAt;
    private final Instant updatedAt;

    public UpdateUserProfileResponseDto(Member member) {
        this.name = member.getName();
        this.role = member.getRole();
        this.rank = member.getRank();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }
}
