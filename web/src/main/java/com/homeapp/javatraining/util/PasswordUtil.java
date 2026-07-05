package com.homeapp.javatraining.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class PasswordUtil {
    private static final String ALGORITHM = "SHA-256";

    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        String computedHash = hashPassword(rawPassword);
        return computedHash.equals(hashedPassword);
    }
}
