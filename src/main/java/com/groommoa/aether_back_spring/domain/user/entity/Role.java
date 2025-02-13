package com.groommoa.aether_back_spring.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    Member("ROLE_MEMBER"),
    Admin("ROLE_ADMIN");

    private final String key;
}
