package com.fourrage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.TypeDevis;

public interface TypeDevisRepository extends JpaRepository<TypeDevis, Long> {
    Optional<TypeDevis> findByLibelleIgnoreCase(String libelle);
}
