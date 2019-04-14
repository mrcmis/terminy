package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface SubjectRepository <T extends Subject> extends JpaRepository<T, Long>, CustomSubjectRepository<T> {
}
