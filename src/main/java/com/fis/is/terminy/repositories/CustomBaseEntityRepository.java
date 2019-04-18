package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.BaseEntity;

public interface CustomBaseEntityRepository<T extends BaseEntity> {
    void saveEncrypted(T baseEntity);
}
