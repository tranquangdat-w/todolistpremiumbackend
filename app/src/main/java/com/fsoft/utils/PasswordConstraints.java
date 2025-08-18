package com.fsoft.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraints {
    String message() default
            "Password must be at least 8 characters long" +
                    " and contain at least one digit, one lowercase," +
                    " one uppercase, and one special character";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}