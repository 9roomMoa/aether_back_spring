package com.groommoa.aether_back_spring.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "testUsers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String profile;

    private String userKey;

    private Role role;

    @Builder
    public User(String name, String email, String profile, String userKey, Role role) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.userKey = userKey;
        this.role = role;
    }

}
