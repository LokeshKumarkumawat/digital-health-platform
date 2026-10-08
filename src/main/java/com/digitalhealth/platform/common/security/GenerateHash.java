package com.digitalhealth.platform.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "health247@123";
        String hash = encoder.encode(password);

        System.out.println("=".repeat(80));
        System.out.println("Password: " + password);
        System.out.println("Hash:     " + hash);
        System.out.println("=".repeat(80));

        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + (matches ? "✅ VALID" : "❌ INVALID"));
    }
}