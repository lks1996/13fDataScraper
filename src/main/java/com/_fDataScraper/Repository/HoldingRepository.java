package com._fDataScraper.Repository;

import com._fDataScraper.Entity.HoldingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<HoldingEntity, Long> {
}
