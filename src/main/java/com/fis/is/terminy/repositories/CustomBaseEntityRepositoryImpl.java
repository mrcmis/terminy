package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class CustomBaseEntityRepositoryImpl<T extends BaseEntity> implements CustomBaseEntityRepository<T> {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void saveEncrypted(T baseEntity) {
        baseEntity.setPassword(encoder.encode(baseEntity.getPassword()));
        entityManager.persist(baseEntity);
    }
}