package com.fourrage.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;

public interface DemandeStatusRepository extends JpaRepository<DemandeStatus, Long> {
    Optional<DemandeStatus> findTopByDemandeOrderByDateChangementDescIdDesc(Demande demande);

    @Query("SELECT ds FROM DemandeStatus ds JOIN FETCH ds.status JOIN FETCH ds.demande ORDER BY ds.dateChangement DESC, ds.id DESC")
    List<DemandeStatus> findAllWithStatusAndDemande();

    @Query("SELECT ds.dateChangement FROM DemandeStatus ds WHERE ds.demande.id = :demandeId ORDER BY ds.dateChangement DESC, ds.id DESC LIMIT 1")
    LocalDateTime findLatestByDemandeId(@Param("demandeId") Long demandeId);
}
