package com.fourrage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fourrage.model.Parametre;

public interface ParametreStatus extends JpaRepository<Parametre, Long> {
    List<Parametre> findByStatusIdDepartOrderByDureeMinimumAsc(Long statusIdDepart);
}
