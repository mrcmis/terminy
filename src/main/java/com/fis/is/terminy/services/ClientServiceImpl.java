package com.fis.is.terminy.services;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("clientService")
public class ClientServiceImpl implements ClientService{

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Optional findClientByEmail(String email) {
        return clientRepository.findByMail(email);
    }

    @Override
    public Optional findClientByResetToken(String resetToken) {
        return clientRepository.findByResetToken(resetToken);
    }

    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }
}
