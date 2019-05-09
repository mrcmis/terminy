package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Reservations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Long> {
    Page<Reservations> findByClientId(Long clientId, Pageable pageable);
   // Optional<Reservations> findByCompanyIdAndClientId(Long companyId, Long clientId);
    List<Reservations> findAllByCompanyIdAndDate(Long companyId, LocalDate date);
}
