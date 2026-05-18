package com.fourrage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;

public interface DemandeStatusRepository extends JpaRepository<DemandeStatus, Long> {
    Optional<DemandeStatus> findTopByDemandeOrderByDateChangementDescIdDesc(Demande demande);
}
