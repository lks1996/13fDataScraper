package com._fDataScraper.Repository;

import com._fDataScraper.Dto.Filer;
import com._fDataScraper.Entity.FilingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilingRepository extends JpaRepository<FilingEntity, String> {

    boolean existsByAccessionNumber(String accessionNumber);

    @Query("SELECT DISTINCT new com._fDataScraper.Dto.Filer(f.cik, f.companyName) FROM FilingEntity f ORDER BY f.companyName ASC")
    List<Filer> findDistinctFilers();

    Optional<FilingEntity> findFirstByCikOrderByPeriodOfReportDescFiledAsOfDateDesc(String cik);
}
