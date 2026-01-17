package com.trackwize.common.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean isPasswordMatch(String reqPassword, String dbPassword) {
        return BCrypt.checkpw(reqPassword, dbPassword);
    }
}
