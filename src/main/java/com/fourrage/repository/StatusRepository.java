package com.fourrage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fourrage.model.District;
import com.fourrage.model.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByLibelleIgnoreCase(String libelle);

}
