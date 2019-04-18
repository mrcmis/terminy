package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseEntityRepository<T extends BaseEntity> extends JpaRepository<T, Long>, CustomBaseEntityRepository<T> {
}
