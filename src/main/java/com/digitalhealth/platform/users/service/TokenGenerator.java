package com.digitalhealth.platform.users.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private static final int TOKEN_LENGTH = 32;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
