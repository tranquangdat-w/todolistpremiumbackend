package com.fsoft.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraints, String> {

    @Override
    public void initialize(PasswordConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (isSpecialCharacter(c)) {
                hasSpecialChar = true;
            }
        }

        return hasDigit && hasLowercase && hasUppercase && hasSpecialChar;
    }

    private boolean isSpecialCharacter(char c) {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        return specialChars.indexOf(c) != -1;
    }
}
