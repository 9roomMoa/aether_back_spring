package com.groommoa.aether_back_spring.domain.user.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

public class SocialUser {

    private Provider provider;

    private String socialId;

    private String email;

    private Instant createdAt;

    private Instant updatedAt;

    public SocialUser(Provider provider, String socialId, String email) {
        Instant now = Instant.now();

        this.provider = provider;
        this.socialId = socialId;
        this.email = email;
        this.createdAt = now;
        this.updatedAt = now;
    }
}
