package com.fis.is.terminy.validation.validators;


import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import com.fis.is.terminy.validation.annotations.UniqueEmailCheck;
import com.fis.is.terminy.validation.annotations.UniqueLoginCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailCheck, String> {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void initialize(UniqueEmailCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return !(clientRepository.findByMail(s).isPresent() || companyRepository.findByMail(s).isPresent());
        } catch (Exception e) {
            return true;
        }
    }
}