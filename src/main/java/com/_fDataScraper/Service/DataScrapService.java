package com._fDataScraper.Service;

import com._fDataScraper.Common.FilingNotFoundException;
import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.FilingEntity;
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
     * 최근 공시 정보 조회.
     * @return Filing 리스트
     */
    public List<Filing> getFilings() throws IOException, InterruptedException {
        // 1. 최근 6개월 날짜 범위 설정.
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 2. 날짜 범위를 사용하여 API URL 생성.
        String url = String.format("%s/filings?from=%s&to=%s&limit=100"
                ,API_BASE_URL
                , weekAgo.format(formatter)
                , today.format(formatter));

        log.info("Getting filings from this url ::  {}", url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch recent filings. Status code: " + response.statusCode());
        }

        Type filingListType = new TypeToken<List<Filing>>(){}.getType();
        return gson.fromJson(response.body(), filingListType);
    }

    /**
     * 특정 공시의 모든 보유 종목 리스트 조회.
     * @param cik 기관의 CIK 번호
     * @param accessionNumber 공시의 고유 번호
     * @return 보유 종목(Holding) 리스트
     */
    public List<Holding> getHoldings(String cik, String accessionNumber) throws IOException, InterruptedException {

        String url = String.format("%s/form?accession_number=%s&cik=%s&limit=100"
                , API_BASE_URL
                , accessionNumber
                , cik);

        log.info("Getting holdings from this url ::  {}", url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch holdings for " + accessionNumber + ". Status code: " + response.statusCode());
        }

        Type holdingListType = new TypeToken<List<Holding>>(){}.getType();
        return gson.fromJson(response.body(), holdingListType);
    }

}
