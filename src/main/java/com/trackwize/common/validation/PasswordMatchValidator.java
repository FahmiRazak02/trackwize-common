package com.trackwize.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) return true;
        try {
            Method getPassword = obj.getClass().getMethod("getPassword");
            Method getConfirm = obj.getClass().getMethod("getConfirmPassword");

            String password = (String) getPassword.invoke(obj);
            String confirm = (String) getConfirm.invoke(obj);

            if (password != null && confirm != null && !password.equals(confirm)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Password and Confirm Password do not match")
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
                return false;
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // optional: log warning
            return true;
        }

        return true;
    }
}
