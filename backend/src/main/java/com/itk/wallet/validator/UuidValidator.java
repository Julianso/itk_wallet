package com.itk.wallet.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UuidValidator implements ConstraintValidator<ValidUuid, UUID> {

    private final String regex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    @Override
    public void initialize(ValidUuid validUuid) { }

    @Override
    public boolean isValid(UUID id, ConstraintValidatorContext cxt) {
        return id.toString().matches(this.regex);
    }
}