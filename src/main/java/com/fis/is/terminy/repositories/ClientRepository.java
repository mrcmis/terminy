package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, CustomClientRepository {
    Optional<Client> findByLogin(String login);
    Optional<Client> findByMail(String mail);
}
