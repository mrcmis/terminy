package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, CustomCompanyRepository {
    Optional<Company> findByLogin(String login);
}