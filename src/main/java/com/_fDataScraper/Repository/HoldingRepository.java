package com._fDataScraper.Repository;

import com._fDataScraper.Entity.HoldingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<HoldingEntity, Long> {

    List<HoldingEntity> findByFilingAccessionNumber(String accessionNumber);
}
