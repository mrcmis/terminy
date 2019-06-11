package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.CompanyWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyWorkplaceRepository extends JpaRepository<CompanyWorkplace, Long> {
    Optional<CompanyWorkplace> findById(Long companyWorkplaceId);
    List<CompanyWorkplace> findAllByCompanyId(Long companyId);
   Optional<CompanyWorkplace> findByCompanyIdAndName(Long companyId, String name);
}
