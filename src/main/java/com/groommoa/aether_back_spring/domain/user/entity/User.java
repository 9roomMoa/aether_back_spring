package com.groommoa.aether_back_spring.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private boolean isSocial;

    private List<SocialUser> socialAccounts;

    private Role role;

    private Rank rank;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder
    public User(String name, String email, boolean isSocial, List<SocialUser> socialAccounts,
                Role role, Rank rank) {
        this.name = name;
        this.email = email;
        this.isSocial = isSocial;
        this.socialAccounts = socialAccounts;
        this.role = role;
        this.rank = rank;

    }

}
