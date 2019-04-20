package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Client;

public interface CustomClientRepository {
    void saveModifiedClient(Client client);
}
