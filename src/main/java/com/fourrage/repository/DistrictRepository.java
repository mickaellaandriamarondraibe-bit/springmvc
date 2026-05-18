package com.fourrage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fourrage.model.District;

public interface DistrictRepository extends JpaRepository<District, Long> {
    @Query("SELECT d FROM District d WHERE d.region.id = :regionId")
    List<District> findByRegionId(@Param("regionId") Long regionId);
}
