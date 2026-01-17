package com.trackwize.common.validation;

import com.trackwize.common.constant.CommonConst;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class PasswordValidatorImpl implements ConstraintValidator<PasswordValidator, Object> {

    public static final String PASSWORD = "password";

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) return true;

        try {
            Method getPassword = obj.getClass().getMethod("getPassword");
            Method getConfirm = obj.getClass().getMethod("getConfirmPassword");

            String password = (String) getPassword.invoke(obj);
            String confirm = (String) getConfirm.invoke(obj);

            if (password == null || confirm == null) return true;

//             Check length
            if (password.length() < 8) {
                buildViolation(context, PASSWORD, "Password must be at least 8 characters long");
                return false;
            }

//             Lowercase
            if (!password.matches(CommonConst.PASSWORD_LOWERCASE_CHAR_REGEX)) {
                buildViolation(context, PASSWORD, "Password must contain at least one lowercase letter");
                return false;
            }

//             Uppercase
            if (!password.matches(CommonConst.PASSWORD_UPPERCASE_CHAR_REGEX)) {
                buildViolation(context, PASSWORD, "Password must contain at least one uppercase letter");
                return false;
            }

//             Digit
            if (!password.matches(CommonConst.PASSWORD_DIGIT_REGEX)) {
                buildViolation(context, PASSWORD, "Password must contain at least one number");
                return false;
            }

//             Special char
            if (!password.matches(CommonConst.PASSWORD_SPECIAL_CHAR_REGEX)) {
                buildViolation(context, PASSWORD, "Password must contain at least one special character");
                return false;
            }

//             Match confirm password
            if (!password.equals(confirm)) {
                buildViolation(context, "confirmPassword", "Password and Confirm Password do not match");
                return false;
            }

        } catch (Exception e) {
            return true;
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String field, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
