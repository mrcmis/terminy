package com.fis.is.terminy.validation.validators;


import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import com.fis.is.terminy.validation.annotations.UniqueLoginCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueLoginValidator implements ConstraintValidator<UniqueLoginCheck, String> {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void initialize(UniqueLoginCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return !(clientRepository.findByLogin(s).isPresent() || companyRepository.findByLogin(s).isPresent());
        } catch (Exception e){
            return true;
        }
    }
}
