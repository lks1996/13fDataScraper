package com._fDataScraper.Mapper;

import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.HoldingEntity;
import org.mapstruct.factory.Mappers;

public interface HoldingMapper {
    HoldingMapper INSTANCE = Mappers.getMapper(HoldingMapper.class);

    HoldingEntity toEntity(Holding holdingDto);
}
