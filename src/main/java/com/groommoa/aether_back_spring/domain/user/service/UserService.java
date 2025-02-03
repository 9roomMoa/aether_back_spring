package com.groommoa.aether_back_spring.domain.user.service;

import com.groommoa.aether_back_spring.domain.user.exception.UserException;
import com.groommoa.aether_back_spring.domain.user.entity.User;
import com.groommoa.aether_back_spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.groommoa.aether_back_spring.global.common.exception.ErrorCode.USER_NOT_FOUND;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * 사용자 정보 조회
     *
     * @param userKey 조회할 사용자의 고유 키
     * @return 사용자 정보가 담긴 Map 객체
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     */
    public Map<String, Object> getUserInfo(String userKey){
        User user = findByUserKeyOrThrow(userKey);

        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("profile", user.getProfile());
        result.put("role", user.getRole());

        return result;
    }

    /**
     * userKey를 기반으로 사용자를 조회하며, 존재하지 않을 경우 예외 발생
     *
     * @param userKey 조회할 사용자의 고유 키
     * @return 조회된 User 엔티티
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     */
    private User findByUserKeyOrThrow(String userKey){
        return userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
