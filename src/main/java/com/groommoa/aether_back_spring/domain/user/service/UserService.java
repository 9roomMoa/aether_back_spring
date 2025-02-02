package com.groommoa.aether_back_spring.domain.user.service;

import com.groommoa.aether_back_spring.domain.user.exception.UserException;
import com.groommoa.aether_back_spring.domain.user.model.User;
import com.groommoa.aether_back_spring.domain.user.model.UserRepository;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.USER_NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Map<String, Object> getUserInfo(String userKey){
        User user = findByUserKeyOrThrow(userKey);

        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("profile", user.getProfile());
        result.put("role", user.getRole());

        return result;
    }

    private User findByUserKeyOrThrow(String userKey){
        return userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
