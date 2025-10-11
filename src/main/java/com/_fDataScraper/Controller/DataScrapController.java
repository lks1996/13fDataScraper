package com._fDataScraper.Controller;

import com._fDataScraper.Service.DataScrapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/data")
public class DataScrapController {

    private final DataScrapService dataScrapService;

    public DataScrapController(DataScrapService dataScrapService) {
        this.dataScrapService = dataScrapService;
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

}
