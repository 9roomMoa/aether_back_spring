package com.groommoa.aether_back_spring.domain.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false, unique = true)
    private String userKey;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
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
