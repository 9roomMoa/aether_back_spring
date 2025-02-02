package com.groommoa.aether_back_spring.domain.user.exception;

import com.groommoa.aether_back_spring.global.common.exception.CustomException;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;

public class UserException extends CustomException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
