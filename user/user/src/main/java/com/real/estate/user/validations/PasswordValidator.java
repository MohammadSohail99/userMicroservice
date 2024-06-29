package com.real.estate.user.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Password must be at least 8 characters long with at least one uppercase letter and one special symbol from @#$!
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[@#$!]).{8,}$");
    }
}
