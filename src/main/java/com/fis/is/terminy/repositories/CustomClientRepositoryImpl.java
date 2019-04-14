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
public class CustomClientRepositoryImpl implements CustomClientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void saveEncryptedClient(Client client) {
        client.setPassword(encoder.encode(client.getPassword()));
        entityManager.persist(client);
    }
}
