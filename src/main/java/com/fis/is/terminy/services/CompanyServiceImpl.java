package com.fis.is.terminy.services;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService{

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Optional findCompanyByEmail(String email) {
        return companyRepository.findByMail(email);
    }

    @Override
    public Optional findCompanyByResetToken(String resetToken) {
        return companyRepository.findByResetToken(resetToken);
    }

    @Override
    public void save(Company company) {
        companyRepository.save(company);
    }
}
