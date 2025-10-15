package com._fDataScraper.Mapper;

import com._fDataScraper.Dto.Filing;
import com._fDataScraper.Entity.FilingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilingMapper {

    @Mapping(target = "holdings", ignore = true)
    FilingEntity toEntity(Filing filingDto);

    @Mapping(target = "holdings", ignore = true)
    List<FilingEntity> toEntityList(List<Filing> filingDtos);
}
