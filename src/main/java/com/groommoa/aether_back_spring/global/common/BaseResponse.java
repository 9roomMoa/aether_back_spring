package com.groommoa.aether_back_spring.global.common;

import lombok.Getter;

import java.util.Map;

@Getter
public class BaseResponse {
    private int code;
    private String message;
    private Map<String, Object> result;

    public BaseResponse(int code, String message, Map<String, Object> result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
}
