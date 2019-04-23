package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class CustomCompanyRepositoryImpl implements CustomCompanyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void saveModifiedCompany(Company company) {
        company.setPassword(encoder.encode(company.getPassword()));
        entityManager.persist(company);
    }
}
