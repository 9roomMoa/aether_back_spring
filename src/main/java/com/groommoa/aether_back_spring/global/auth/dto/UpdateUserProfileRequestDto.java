package com.groommoa.aether_back_spring.global.auth.dto;

import com.groommoa.aether_back_spring.domain.user.entity.Rank;
import lombok.Getter;

@Getter
public class UpdateUserProfileRequestDto {

    private String name;
    private Rank rank;
}
