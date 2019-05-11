package com.fis.is.terminy.repositories;

import com.fis.is.terminy.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByPrivilege(String privilege);
}
