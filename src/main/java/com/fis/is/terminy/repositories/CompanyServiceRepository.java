package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyServiceRepository extends JpaRepository<CompanyService, Long> {
    Page<CompanyService> findByCompanyId(Long companyId, Pageable pageable);
    Optional<CompanyService> findByIdAndCompanyId(Long id, Long companyId);
    List<CompanyService> findAllByCompanyId(Long companyId);

}
