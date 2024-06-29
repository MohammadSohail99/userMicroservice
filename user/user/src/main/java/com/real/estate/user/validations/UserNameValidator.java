package com.real.estate.user.validations;

import com.real.estate.user.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UserNameValidator implements ConstraintValidator<ValidUserName, String> {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(ValidUserName constraintAnnotation) {
        // Initialization code if needed
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {
        if (userName == null || userName.length() <= 6 ) {
            String messageKey = "user.username.length";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
