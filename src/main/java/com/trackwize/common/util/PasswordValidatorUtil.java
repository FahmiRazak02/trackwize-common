package com.trackwize.common.util;

import com.trackwize.common.constant.CommonConst;

public class PasswordValidatorUtil {

    private PasswordValidatorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String validatePasswordRules(String password) {
        if (password.length() < CommonConst.PASSWORD_MIN_LENGTH)
            return "Password must be at least 8 characters long";

        if (!password.matches(CommonConst.PASSWORD_UPPERCASE_CHAR_REGEX))
            return "Password must contain at least one uppercase letter";

        if (!password.matches(CommonConst.PASSWORD_LOWERCASE_CHAR_REGEX))
            return "Password must contain at least one lowercase letter";

        if (!password.matches(CommonConst.PASSWORD_DIGIT_REGEX))
            return "Password must contain at least one number";

        if (!password.matches(CommonConst.PASSWORD_SPECIAL_CHAR_REGEX))
            return "Password must contain at least one special character";

        if (password.contains(" "))
            return "Password must not contain whitespace";

        return null;
    }

    public static String validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            return "Password and Confirm Password are not match";

        return null;
    }
}
