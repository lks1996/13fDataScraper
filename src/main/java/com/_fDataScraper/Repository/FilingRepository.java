package com._fDataScraper.Repository;

import com._fDataScraper.Entity.FilingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilingRepository extends JpaRepository<FilingEntity, String> {
}
