package com._fDataScraper.Service;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.FilingEntity;
import com._fDataScraper.Entity.HoldingEntity;
import com._fDataScraper.Mapper.FilingMapper;
import com._fDataScraper.Mapper.HoldingMapper;
import com._fDataScraper.Repository.FilingRepository;
import com._fDataScraper.Repository.HoldingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class DataSaveService {
    private final FilingRepository filingRepository;
    private final HoldingRepository holdingRepository;
    private final FilingMapper filingMapper;
    private final HoldingMapper holdingMapper;

    @Autowired
    public DataSaveService(FilingRepository filingRepository
            , HoldingRepository holdingRepository
            , FilingMapper filingMapper
            , HoldingMapper holdingMapper) {
        this.filingRepository = filingRepository;
        this.holdingRepository = holdingRepository;
        this.filingMapper = filingMapper;
        this.holdingMapper = holdingMapper;
    }

    /**
     * Filing DTO를 받아 DB에 저장.
     * 중복된 경우 저장 스킵.
     * @param filingDto 저장할 Filing DTO
     * @return 저장된 FilingEntity. 이미 존재하면 Optional.empty()를 반환.
     */
    public Optional<FilingEntity> saveFiling(Filing filingDto) {
        if (filingRepository.existsByAccessionNumber(filingDto.accessionNumber())) {
            log.info("Filing with Accession Number {} already exists. Skipping save.", filingDto.accessionNumber());
            return Optional.empty();
        }

        // 1. FilingDto -> FilingEntity 변환.
        FilingEntity filingEntity = filingMapper.toEntity(filingDto);

        // 2. DB 저장.
        FilingEntity savedEntity = filingRepository.save(filingEntity);
        log.info("Successfully saved Filing: {}", filingEntity.getAccessionNumber());
        return Optional.of(savedEntity);
    }

    /**
     * Holding DTO 리스트를 받아 부모 FilingEntity와 관계를 맺고 DB에 저장.
     * @param parentFiling 부모 FilingEntity
     * @param holdingDtos 저장할 Holding DTO 리스트
     */
    public List<HoldingEntity> saveHoldings(FilingEntity parentFiling, List<Holding> holdingDtos) {
        if (holdingDtos == null || holdingDtos.isEmpty()) {
            log.info("No holdings to save for filing {}", parentFiling.getAccessionNumber());
            return Collections.emptyList();
        }

        // 1. HoldingDtos -> HoldingEntityList 변환.
        List<HoldingEntity> holdingEntities = holdingMapper.toEntityList(holdingDtos);

        // 2. 변환된 각 Entity에 부모-자식 관계 설정.
        holdingEntities.forEach(entity -> entity.setFiling(parentFiling));

        // 3. DB 저장.
        List<HoldingEntity> savedHoldings = holdingRepository.saveAll(holdingEntities);
        log.info("Successfully saved holdings {} ", holdingEntities.size());

        // 4. 저장한 데이터 리턴.
        return savedHoldings;
    }

    /**
     * Accession Number로 저장된 Holding 리스트를 조회.
     * @param accessionNumber 조회할 공시의 고유번호
     * @return 조회된 HoldingEntity 리스트
     */
    public List<HoldingEntity> getHoldingsByAccessionNumber(String accessionNumber) {
        log.info("Fetching existing holdings for accession number: {}", accessionNumber);
        return holdingRepository.findByFilingAccessionNumber(accessionNumber);
    }

}
