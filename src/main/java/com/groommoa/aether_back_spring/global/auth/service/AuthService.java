package com.groommoa.aether_back_spring.global.auth.service;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.domain.user.service.UserService;
import com.groommoa.aether_back_spring.global.auth.dto.UpdateUserProfileRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public Member getUserProfile(String userId){
        return userService.getUserProfile(userId);
    }

    public Member updateUserProfile(String userId, UpdateUserProfileRequestDto request){
        return userService.updateUserProfile(userId, request);
    }
}
