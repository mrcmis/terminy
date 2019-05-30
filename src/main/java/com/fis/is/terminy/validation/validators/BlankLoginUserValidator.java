package com.fis.is.terminy.validation.validators;

import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.validation.annotations.BlankLoginUserCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class BlankLoginUserValidator implements ConstraintValidator<BlankLoginUserCheck, String> {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public void initialize(BlankLoginUserCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return !(clientRepository.findByLogin(s).isPresent());
        } catch (Exception e){
            return true;
        }
    }
}
