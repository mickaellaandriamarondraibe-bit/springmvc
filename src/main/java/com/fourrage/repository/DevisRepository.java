package com.fourrage.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.Devis;
import com.fourrage.model.Demande;
import java.util.List;

public interface DevisRepository extends JpaRepository<Devis, Long> {
    @EntityGraph(attributePaths = {"type"})
    List<Devis> findByDemande(Demande demande);
    boolean existsByDemandeAndTypeId(Demande demande, Long typeId);
}
