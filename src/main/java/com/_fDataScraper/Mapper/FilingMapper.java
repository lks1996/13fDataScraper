package com._fDataScraper.Mapper;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Entity.FilingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FilingMapper {

    @Mapping(target = "holdings", ignore = true)
    FilingEntity toEntity(Filing filingDto);
}
