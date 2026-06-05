package com.fourrage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;

public interface DemandeStatusRepository extends JpaRepository<DemandeStatus, Long> {
    @EntityGraph(attributePaths = {"status", "demande"})
    Optional<DemandeStatus> findTopByDemandeOrderByIdDesc(Demande demande);

    @Query("""
            SELECT ds
            FROM DemandeStatus ds
            JOIN FETCH ds.status
            JOIN FETCH ds.demande
            WHERE ds.demande = :demande
            ORDER BY ds.id ASC
            """)
    List<DemandeStatus> findByDemandeOrderByIdAsc(@Param("demande") Demande demande);

    @Query("SELECT ds FROM DemandeStatus ds JOIN FETCH ds.status JOIN FETCH ds.demande ORDER BY ds.dateChangement DESC, ds.id DESC")
    List<DemandeStatus> findAllWithStatusAndDemande();
}
