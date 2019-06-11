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
    Optional<Reservations> findById(Long id);
    Optional<Reservations> findByIdAndClientId( Long id, Long clientId);
    List<Reservations> findAllByCompanyWorkplaceId(Long companyWorkplaceId);
    List<Reservations> findAllByCompanyWorkplaceIdAndDate(Long companyWorkplaceId, LocalDate date);
    List<Reservations> findAllByClientIdAndCompanyWorkplaceId(Long clientId, Long companyWorkplaceId);
    //Optional<Reservations> findByIdAndCompanyId( Long id, Long companyId);
   // List<Reservations> findByCompanyIdAndClientId(Long companyId, Long clientId);
   // List<Reservations> findAllByCompanyIdAndDate(Long companyId, LocalDate date);
    //List<Reservations> findAllByCompanyId(Long companyId);
}
