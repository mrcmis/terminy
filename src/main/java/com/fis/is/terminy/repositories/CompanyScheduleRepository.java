package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.CompanySchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyScheduleRepository extends JpaRepository<CompanySchedule, Long> {
    Page<CompanySchedule> findByCompanyId(Long companyId, Pageable pageable);
    Optional<CompanySchedule> findByCompanyIdAndDay(Long companyId, String day);
}
