package com.groommoa.aether_back_spring.global.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DtoUtils {

    @Autowired
    private ObjectMapper injectedObjectMapper;

    private static ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = injectedObjectMapper;
    }

    public static Map<String, Object> toMap(Object dto){
        return objectMapper.convertValue(dto, new TypeReference<>() {});
    }
}