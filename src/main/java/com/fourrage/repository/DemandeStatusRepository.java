package com.fourrage.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;

public interface DemandeStatusRepository extends JpaRepository<DemandeStatus, Long> {
    Optional<DemandeStatus> findTopByDemandeOrderByDateChangementDescIdDesc(Demande demande);

    @Query("SELECT ds.dateChangement FROM DemandeStatus ds WHERE ds.demande.id = :demandeId ORDER BY ds.dateChangement DESC, ds.id DESC LIMIT 1")
    LocalDateTime findLatestByDemandeId(@Param("demandeId") Long demandeId);
}
