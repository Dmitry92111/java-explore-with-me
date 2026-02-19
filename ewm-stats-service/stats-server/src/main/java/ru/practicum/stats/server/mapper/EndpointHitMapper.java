package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitCreateDto;
import ru.practicum.stats.server.entity.EndpointHit;

public class EndpointHitMapper {
    private EndpointHitMapper() {
    }

    public static EndpointHit fromDto(EndpointHitCreateDto dto) {
        return EndpointHit.of(
                dto.app(),
                dto.uri(),
                dto.ip(),
                dto.timestamp()
        );
    }
}
