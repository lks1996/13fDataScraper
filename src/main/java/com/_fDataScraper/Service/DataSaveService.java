package com._fDataScraper.Service;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.FilingEntity;
import com._fDataScraper.Entity.HoldingEntity;
import com._fDataScraper.Mapper.FilingMapper;
import com._fDataScraper.Repository.FilingRepository;
import com._fDataScraper.Repository.HoldingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataSaveService {
    private final FilingRepository filingRepository;
    private final HoldingRepository holdingRepository;

    @Autowired
    public DataSaveService(FilingRepository filingRepository, HoldingRepository holdingRepository) {
        this.filingRepository = filingRepository;
        this.holdingRepository = holdingRepository;
    }

    /**
     * Filing DTO를 받아 DB에 저장합니다.
     * 중복된 경우 저장을 건너뜁니다.
     * @param filingDto 저장할 Filing DTO
     * @return 저장된 FilingEntity. 이미 존재하면 Optional.empty()를 반환합니다.
     */
    public Optional<FilingEntity> saveFiling(Filing filingDto) {
        if (filingRepository.existsByAccessionNumber(filingDto.accessionNumber())) {
            log.info("Filing with Accession Number {} already exists. Skipping save.", filingDto.accessionNumber());
            return Optional.empty();
        }

        FilingEntity filingEntity = FilingMapper.INSTANCE.toEntity(filingDto);

        try {
            filingEntity.setPeriodOfReport(new SimpleDateFormat("yyyy-MM-dd").parse(filingDto.periodOfReport()));
        } catch (ParseException e) {
            log.warn("Could not parse date: {}", filingDto.periodOfReport(), e);
        }
        filingEntity.setTableValueTotal(filingDto.tableValueTotal());
        filingEntity.setTableEntryTotal(filingDto.tableEntryTotal());

        FilingEntity savedEntity = filingRepository.save(filingEntity);
        log.info("Successfully saved Filing: {}", filingDto.getAccessionNumber());
        return Optional.of(savedEntity);
    }

    /**
     * Holding DTO 리스트를 받아 부모 FilingEntity와 관계를 맺고 DB에 저장합니다.
     * @param parentFiling 부모가 되는 FilingEntity (DB에 이미 저장된 상태여야 함)
     * @param holdingDtos 저장할 Holding DTO 리스트
     */
    public void saveHoldings(FilingEntity parentFiling, List<Holding> holdingDtos) {
        if (holdingDtos == null || holdingDtos.isEmpty()) {
            log.info("No holdings to save for filing {}", parentFiling.getAccessionNumber());
            return;
        }

        List<HoldingEntity> holdingEntities = holdingDtos.stream()
                .map(dto -> {
                    HoldingEntity entity = new HoldingEntity();
                    entity.setTicker(dto.ticker());
                    entity.setNameOfIssuer(dto.nameOfIssuer());
                    entity.setShares(dto.shares());
                    entity.setValue(dto.value());
                    // 가장 중요한 부분: 자식 엔티티에 부모 엔티티를 설정하여 관계를 맺어줍니다.
                    entity.setFiling(parentFiling);
                    return entity;
                })
                .collect(Collectors.toList());

        // saveAll을 사용하여 한 번의 쿼리로 여러 데이터를 효율적으로 저장합니다.
        holdingRepository.saveAll(holdingEntities);
        log.info("Successfully saved holdings {} ", holdingEntities.size());
    }

}
