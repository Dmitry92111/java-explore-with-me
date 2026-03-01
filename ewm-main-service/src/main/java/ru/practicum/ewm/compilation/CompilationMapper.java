package ru.practicum.ewm.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "id", ignore = true)
    Compilation toEntity(NewCompilationDto dto);

    @Mapping(target = "events", source = "eventShortDtos")
    CompilationDto toDto(Compilation compilation, Set<EventShortDto> eventShortDtos);
}
