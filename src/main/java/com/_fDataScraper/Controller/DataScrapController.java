package com._fDataScraper.Controller;

import com._fDataScraper.Common.ApiResponse;
import com._fDataScraper.Entity.HoldingEntity;
import com._fDataScraper.Service.DataScrapService;
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
    private final FilingProcessService filingProcessService;

    public DataScrapController(DataScrapService dataScrapService, FilingProcessService filingProcessService) {
        this.dataScrapService = dataScrapService;
        this.filingProcessService = filingProcessService;
    }

    /**
     * 13f 공시 데이터 기관 목록 조회
     */

    /**
     * 13f 공시 데이터 기관 보유 자산 업데이트 조회
     * /api/v1/filings
     */
    @GetMapping("/getLatestFiling")
    public void getLatestFiling(String cik) throws IOException, InterruptedException {
        dataScrapService.getLatestFiling(cik);
    }

    /**
     * 13f 공시 데이터 특정 기관의 보유 자산 조회
     * /api/v1/form
     */
    @GetMapping("/getHolding")
    public void getHoldings(String cik, String accessionNumber) throws IOException, InterruptedException {
        dataScrapService.getHoldings(cik, accessionNumber);
    }

    /**
     * 전체 프로세트 수행.
     */
    @GetMapping("/executeProcess")
    public ResponseEntity<ApiResponse<List<HoldingEntity>>> executeProcess(String cik) throws IOException, InterruptedException {
        List<HoldingEntity> result = filingProcessService.processLatestFilingByCik(cik);
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.OK);
    }
}
