package com.groommoa.aether_back_spring.global.common.utils;

import java.util.UUID;

public final class KeyGenerator {

    public static String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
