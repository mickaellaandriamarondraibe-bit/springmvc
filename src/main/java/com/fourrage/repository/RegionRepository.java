package com.fourrage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fourrage.model.Region;
public interface RegionRepository extends JpaRepository<Region, Long> {
    
}
