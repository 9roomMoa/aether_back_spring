package com.groommoa.aether_back_spring.global.auth.exception;

import com.groommoa.aether_back_spring.global.common.exception.CustomException;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
