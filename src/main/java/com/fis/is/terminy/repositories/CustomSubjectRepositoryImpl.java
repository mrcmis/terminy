package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class CustomSubjectRepositoryImpl<T extends Subject> implements CustomSubjectRepository<T> {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void saveEncrypted(T subject) {
        subject.setPassword(encoder.encode(subject.getPassword()));
        entityManager.persist(subject);
    }
}