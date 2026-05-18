package com.fourrage.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fourrage.model.Commune;

public interface CommuneRepository extends JpaRepository<Commune, Long> {
    @Query("SELECT c FROM Commune c WHERE c.district.id = :districtId")
    List<Commune> findByDistrictId(@Param("districtId") Long districtId);
}
