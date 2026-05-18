package com.fourrage.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fourrage.model.Demande;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findByClientDetailsId(Long clientId);
    List<Demande> findByClientDetailsNomContainingIgnoreCase(String nom);
}
