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
    private final FilingPersistenceService persistenceService;

    public FilingProcessService(DataScrapService scrapService, FilingPersistenceService persistenceService) {
        this.scrapService = scrapService;
        this.persistenceService = persistenceService;
    }

    /**
     * 특정 기간에 대한 최신 13F Filing 데이터 조회.
     */
    @Transactional
    public void processLatestFilings() throws IOException, InterruptedException {

        log.info("[START] Processing for processLatestFilings");

        // 1. [조회] 특정 기간의 Filing 리스트 API 요청.
        List<Filing> filingDtos = scrapService.getFilings();

        // 2. [저장] Filing 리스트 DB 저장.
        List<FilingEntity> savedFilings = persistenceService.saveFilings(filingDtos);

        log.info("[SUCCESS] Processing finished for processLatestFilings");

    }

    /**
     * 특정 CIK에 대한 최신 13F 공시 데이터 조회.
     * @param cik 처리할 기관의 CIK 번호
     */
    @Transactional
    public List<HoldingEntity> getOrFetchHoldingsByCik(String cik) throws IOException, InterruptedException {

        log.info("[START] Processing for CIK: {}", cik);

        // 1. DB에서 cik 기관의 가장 최신 Filing 데이터 조회.
        FilingEntity latestFilingDto = persistenceService.getLatestFilingByCik(cik);
        String accessionNumber = latestFilingDto.getAccessionNumber();

        // 2. Filing의 accession number로 저장된 Holding 데이터가 DB에 있는지 확인.
        List<HoldingEntity> holdings= persistenceService.getHoldingsByAccessionNumber(accessionNumber);

        // 3. 없으면 api 호출해서 데이터 받아와서 DB에 저장.
        if(holdings.isEmpty()){
            List<Holding> holdingDtos = scrapService.getHoldings(latestFilingDto.getCik(), accessionNumber);
            holdings = persistenceService.saveHoldings(latestFilingDto,holdingDtos);
        }

        // 4. 리턴.
        log.info("[SUCCESS] Processing finished for CIK: {}", cik);
        return holdings;
    }
}
