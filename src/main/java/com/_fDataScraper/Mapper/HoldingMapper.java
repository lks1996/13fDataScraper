package com._fDataScraper.Mapper;

import com._fDataScraper.Dto.Holding;
import com._fDataScraper.Entity.HoldingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HoldingMapper {

    @Mapping(target = "holdingId", ignore = true)
    @Mapping(target = "filing", ignore = true)
    @Mapping(source = "sshPrnamt", target = "shares")
    HoldingEntity toEntity(Holding holdingDto);

    List<HoldingEntity> toEntityList(List<Holding> holdingDtos);
}
