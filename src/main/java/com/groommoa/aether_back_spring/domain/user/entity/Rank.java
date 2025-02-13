package com.groommoa.aether_back_spring.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rank {
    Intern("RANK_INTERN"),
    Junior("RANK_JUNIOR"),
    Senior("RANK_SENIOR"),
    Manager("RANK_MANAGER"),
    Director("RANK_DIRECTOR"),
    VP("RANK_VP"),
    CEO("RANK_CEO");

    private final String key;
}
