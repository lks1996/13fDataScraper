package com._fDataScraper.Repository;

import com._fDataScraper.Entity.FilingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilingRepository extends JpaRepository<FilingEntity, String> {

    boolean existsByAccessionNumber(String accessionNumber);
}
