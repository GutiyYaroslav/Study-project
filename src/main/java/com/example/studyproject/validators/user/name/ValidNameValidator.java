package com.example.studyproject.validators.user.name;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.matches("^[a-zA-Z]*$");
    }
}