package com.fis.is.terminy.services;

import com.fis.is.terminy.models.Company;

import java.util.Optional;

public interface CompanyService {
    Optional<Company> findCompanyByEmail(String email);
    Optional<Company> findCompanyByResetToken(String resetToken);
    void save(Company company);
}
