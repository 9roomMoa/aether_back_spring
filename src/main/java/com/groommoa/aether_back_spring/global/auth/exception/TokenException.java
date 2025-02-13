package com.groommoa.aether_back_spring.global.auth.exception;

import com.groommoa.aether_back_spring.global.common.exception.CustomException;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;

public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
