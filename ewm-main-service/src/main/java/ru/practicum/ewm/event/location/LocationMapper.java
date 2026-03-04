package ru.practicum.ewm.event.location;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    Location toEntity(LocationDto dto);

    LocationDto toDto(Location location);
}
