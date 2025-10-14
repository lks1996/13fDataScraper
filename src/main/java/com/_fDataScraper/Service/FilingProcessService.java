package com._fDataScraper.Service;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.FilingEntity;
import com._fDataScraper.Entity.HoldingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public List<HoldingEntity> processLatestFilingByCik(String cik) throws IOException, InterruptedException {

        log.info("[START] Processing for CIK: {}", cik);

        // 1. [조회] API 응답 -> DTO 변환 .
        Filing latestFilingDto = scrapService.getLatestFiling(cik);
        List<Holding> holdingDtos = scrapService.getHoldings(latestFilingDto.cik(), latestFilingDto.accessionNumber());

        // 2. [저장] Filing 정보를 먼저 저장.
        Optional<FilingEntity> savedFilingOptional = saveService.saveFiling(latestFilingDto);

        List<HoldingEntity> resultHoldings;

        // 3. [저장] Filing이 '새로' 저장된 경우에만 Holding 정보 저장.
        if (savedFilingOptional.isPresent()) {
            FilingEntity savedFiling = savedFilingOptional.get();
            log.info("New filing found. Proceeding to save holdings...");
            resultHoldings = saveService.saveHoldings(savedFiling, holdingDtos);
        // 3. [조회] 이미 저장되어 있는 Holding 정보를 조회.
        } else {
            log.info("This filing already exists.");
            resultHoldings = saveService.getHoldingsByAccessionNumber(latestFilingDto.accessionNumber());
        }

        log.info("[SUCCESS] Processing finished for CIK: {}", cik);
        return resultHoldings;
    }
}
