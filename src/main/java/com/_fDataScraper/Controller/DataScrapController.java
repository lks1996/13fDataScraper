package com._fDataScraper.Controller;

import com._fDataScraper.Common.ApiResponse;
import com._fDataScraper.Entity.HoldingEntity;
import com._fDataScraper.Service.DataScrapService;
import com._fDataScraper.Service.FilingPersistenceService;
import com._fDataScraper.Service.FilingProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataScrapController {

    private final DataScrapService dataScrapService;
    private final FilingPersistenceService persistenceService;
    private final FilingProcessService filingProcessService;

    public DataScrapController(DataScrapService dataScrapService
            , FilingPersistenceService persistenceService
            , FilingProcessService filingProcessService) {
        this.dataScrapService = dataScrapService;
        this.persistenceService = persistenceService;
        this.filingProcessService = filingProcessService;
    }

    /**
     * 13f 공시 데이터 기관 목록 조회
     */
    @GetMapping("/getFilings")
    public void getFilings() throws IOException, InterruptedException {
        dataScrapService.getFilings();
    }

    /**
     * 13f 공시 데이터 기관 보유 자산 업데이트 조회
     * /api/v1/filings
     */
    @GetMapping("/getLatestFiling")
    public void getLatestFiling(String cik) {
        persistenceService.getLatestFilingByCik(cik);
    }

    /**
     * 13f 공시 데이터 특정 기관의 보유 자산 조회
     * /api/v1/form
     */
    @GetMapping("/getHoldings")
    public void getHoldings(String cik, String accessionNumber) throws IOException, InterruptedException {
        dataScrapService.getHoldings(cik, accessionNumber);
    }

    /**
     * 최신 Filing 데이터 조회 및 저장.
     */
    @GetMapping("/executeProcessLatestFilings")
    public void executeProcessLatestFilings() throws IOException, InterruptedException {
        filingProcessService.processLatestFilings();
    }

    /**
     * 특정 기관의 cik 로 최신 공시 데이터 조회 및 저장.
     */
    @GetMapping("/executeProcessHoldingsByCik")
    public ResponseEntity<ApiResponse<List<HoldingEntity>>> executeProcessHoldingsByCik(String cik) throws IOException, InterruptedException {
        List<HoldingEntity> result = filingProcessService.getOrFetchHoldingsByCik(cik);
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.OK);
    }
}
