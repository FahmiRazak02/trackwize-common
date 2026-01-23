package com.trackwize.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating cryptographically secure opaque tokens.
 *
 * Use cases:
 * - Email verification
 * - Password reset
 * - Invite links
 * - One-time actions
 *
 * NOTE:
 * - Tokens are URL-safe
 * - No encoding required
 * - No embedded data
 */
public class OpaqueUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final int TOKEN_BYTE_LENGTH = 16;

    private OpaqueUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generates a URL-safe opaque token.
     *
     * @return opaque token string
     */
    public static String generate() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}

