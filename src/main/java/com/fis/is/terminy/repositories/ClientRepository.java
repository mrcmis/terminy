package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, CustomClientRepository {
}
