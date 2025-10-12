package com._fDataScraper.Mapper;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Entity.FilingEntity;
import org.mapstruct.factory.Mappers;

public interface FilingMapper {
    FilingMapper INSTANCE = Mappers.getMapper(FilingMapper.class);

    FilingEntity toEntity(Filing filingDto);
}
