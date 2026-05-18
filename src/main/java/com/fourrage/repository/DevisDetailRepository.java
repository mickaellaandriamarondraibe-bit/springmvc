package com.fourrage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.Devis;
import com.fourrage.model.DevisDetail;

public interface DevisDetailRepository extends JpaRepository<DevisDetail, Long> {
    List<DevisDetail> findByDevis(Devis devis);
}
