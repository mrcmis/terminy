package com.fis.is.terminy.services;

import com.fis.is.terminy.models.Client;

import java.util.Optional;

public interface ClientService {
    Optional<Client> findClientByEmail(String email);
    Optional<Client> findClientByResetToken(String resetToken);
    void save(Client client);
}
