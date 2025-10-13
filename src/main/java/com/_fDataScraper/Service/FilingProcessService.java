package com._fDataScraper.Service;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.FilingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilingProcessService {

    // final 키워드를 사용하여 불변성을 보장하고, 생성자 주입을 사용합니다.
    private final DataScrapService scrapService;
    private final DataSaveService saveService;

    public FilingProcessService(DataScrapService scrapService, DataSaveService saveService) {
        this.scrapService = scrapService;
        this.saveService = saveService;
    }

    /**
     * 특정 CIK에 대한 최신 13F 공시 데이터 조회 및 DB 적재.
     * @param cik 처리할 기관의 CIK 번호
     */
    @Transactional
    public void processLatestFilingByCik(String cik) {
        try {
            log.info("▶▶▶ [START] Processing for CIK: {}", cik);

            // 1. [조회] API 응답 -> DTO 변환 .
            Filing latestFilingDto = scrapService.getLatestFiling(cik);
            List<Holding> holdingDtos = scrapService.getHoldings(latestFilingDto.cik(), latestFilingDto.accessionNumber());

            // 2. [저장] Filing 정보를 먼저 저장합니다.
            Optional<FilingEntity> savedFilingOptional = saveService.saveFiling(latestFilingDto);

            // 3. [저장] Filing이 '새로' 저장된 경우에만 Holding 정보를 저장합니다.
            if (savedFilingOptional.isPresent()) {
                FilingEntity savedFiling = savedFilingOptional.get();
                log.info("New filing found. Proceeding to save holdings...");
                saveService.saveHoldings(savedFiling, holdingDtos);
            } else {
                log.info("This filing already exists. No holdings were saved.");
            }

            log.info("◀◀◀ [SUCCESS] Processing finished for CIK: {}", cik);

        } catch (Exception e) {
            log.error("XXX [FAIL] An error occurred during processing for CIK {}: {}", cik, e.getMessage());
            throw new RuntimeException("Process failed for CIK " + cik, e);
        }
    }
}
