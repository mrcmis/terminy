package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Subject;

public interface CustomSubjectRepository<T extends Subject> {
    void saveEncrypted(T subject);
}
