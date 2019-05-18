package com.fis.is.terminy.converters;

import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BaseEntityConverter implements Converter<String, BaseEntity> {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public BaseEntity convert(String s) {
        BaseEntity logged;
        Optional<Client> optionalClient = clientRepository.findById(new Long(s));
        Optional<Company> optionalCompany = companyRepository.findById(new Long(s));

        if (optionalClient.isPresent()) {
            logged = optionalClient.get();
        } else if (optionalCompany.isPresent()){
            logged = optionalCompany.get();
        } else{
            throw new UsernameNotFoundException("Login not found");
        }

        return logged;
    }
}
