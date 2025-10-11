package com._fDataScraper.Service;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DataScrapService {

    private static final String API_BASE_URL = "https://forms13f.com/api/v1";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    /**
     * 특정 CIK의 가장 최근 공시 정보를 가져옵니다.
     * @param cik 기관의 CIK 번호
     * @return 가장 최근 Filing 정보
     */
    public Filing getLatestFiling(String cik) throws IOException, InterruptedException {
        // 1. 최근 1년간의 날짜 범위를 설정합니다.
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 2. 날짜 범위를 사용하여 API URL을 생성합니다.
        // limit을 충분히 주어 1년치 데이터를 최대한 가져옵니다. (API 최대치에 따라 조정 필요)
        String url = String.format("%s/filings?cik=%s&from=%s&to=%s&limit=100",
                API_BASE_URL,
                cik,
                oneYearAgo.format(formatter),
                today.format(formatter));

        log.info("Getting latest filing from this url ::  {}", url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch recent filings. Status code: " + response.statusCode());
        }

        Type filingListType = new TypeToken<List<Filing>>(){}.getType();
        List<Filing> allRecentFilings = gson.fromJson(response.body(), filingListType);

        if (allRecentFilings == null || allRecentFilings.isEmpty()) {
            throw new RuntimeException("No recent filings found in the last year.");
        }

        // 3. 전체 리스트에서 원하는 CIK를 가진 첫 번째 공시를 찾습니다.
        Optional<Filing> latestFilingOptional = allRecentFilings.stream()
                .filter(filing -> cik.equals(filing.cik()))
                .findFirst();

        // 4. Optional에 값이 있는지 확인하고 처리합니다.
        if (latestFilingOptional.isPresent()) {
            Filing foundFiling = latestFilingOptional.get();
            log.info("Found matching filing for CIK {}: {}", cik, foundFiling);
            return foundFiling;
        } else {
            throw new RuntimeException("No filings found for CIK: " + cik + " in the last year.");
        }
    }

    /**
     * 특정 공시의 모든 보유 종목 리스트를 가져옵니다.
     * @param cik 기관의 CIK 번호
     * @param accessionNumber 공시의 고유 번호
     * @return 보유 종목(Holding) 리스트
     */
    public List<Holding> getHoldings(String cik, String accessionNumber) throws IOException, InterruptedException {
        // 수정된 URL: /form 엔드포인트를 사용하고 accession_number와 cik를 쿼리 파라미터로 전달합니다.
        // API가 많은 데이터를 페이지로 나눠서 줄 수 있으므로 limit을 충분히 크게 설정합니다.
        String url = String.format("%s/form?accession_number=%s&cik=%s&limit=1000", API_BASE_URL, accessionNumber, cik);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch holdings for " + accessionNumber + ". Status code: " + response.statusCode());
        }

        Type holdingListType = new TypeToken<List<Holding>>(){}.getType();
        return gson.fromJson(response.body(), holdingListType);
    }

}
