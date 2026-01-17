package com.trackwize.common.constant;

public class CommonConst {

    private CommonConst() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String CONTACT_NO_REGEX = "^\\+?[0-9]{7,15}$";

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_UPPERCASE_CHAR_REGEX = ".*[A-Z].*";
    public static final String PASSWORD_LOWERCASE_CHAR_REGEX = ".*[a-z].*";
    public static final String PASSWORD_DIGIT_REGEX = ".*\\d.*";
    public static final String PASSWORD_SPECIAL_CHAR_REGEX = ".*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
}
