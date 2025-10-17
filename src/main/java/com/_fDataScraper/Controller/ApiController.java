package com._fDataScraper.Controller;

import com._fDataScraper.Common.ApiResponse;
import com._fDataScraper.Dto.Filer;
import com._fDataScraper.Entity.HoldingEntity;
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
@RequestMapping("/api")
public class ApiController {

    private final FilingPersistenceService persistenceService;
    private final FilingProcessService filingProcessService;

    public ApiController(FilingPersistenceService persistenceService, FilingProcessService filingProcessService) {
        this.persistenceService = persistenceService;
        this.filingProcessService = filingProcessService;
    }

    /**
     * 구글 시트 드롭다운을 채우기 위한, DB에 저장된 모든 기관 목록 반환.
     * @return ApiResponse에 담긴 Filer 리스트
     */
    @GetMapping("/filers")
    public ResponseEntity<ApiResponse<List<Filer>>> getAllFilers() {
        List<Filer> filers = persistenceService.findAllFilers();
        return new ResponseEntity<>(ApiResponse.success(filers), HttpStatus.OK);
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
