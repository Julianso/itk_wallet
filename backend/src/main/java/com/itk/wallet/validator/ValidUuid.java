package com.itk.wallet.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
public @interface ValidUuid {

    String message() default "{invalid.uuid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}