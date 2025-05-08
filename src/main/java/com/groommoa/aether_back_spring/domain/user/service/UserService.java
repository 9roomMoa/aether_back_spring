package com.groommoa.aether_back_spring.domain.user.service;

import com.groommoa.aether_back_spring.domain.user.entity.Member;
import com.groommoa.aether_back_spring.domain.user.exception.UserException;
import com.groommoa.aether_back_spring.domain.user.repository.UserRepository;
import com.groommoa.aether_back_spring.global.auth.dto.UpdateUserProfileRequestDto;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Member updateUserProfile(String userId, UpdateUserProfileRequestDto request){
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        if (request.getName() != null) member.setName(request.getName());
        if (request.getRank() != null) member.setRank(request.getRank());

        return userRepository.save(member);
    }
}
