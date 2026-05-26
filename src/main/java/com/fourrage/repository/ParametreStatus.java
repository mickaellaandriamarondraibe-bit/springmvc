package com.fourrage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.Parametre;

public interface ParametreStatus extends JpaRepository<Parametre, Long> {
    Optional<Parametre> findTopByStatusIdDepartAndDureeMinimumLessThanEqualOrderByDureeMinimumDesc(Long statusIdDepart, Integer dureeHeures);

    Optional<Parametre> findTopByStatusIdDepartOrderByDureeMinimumAsc(Long statusIdDepart);
}
