package ru.practicum.stats.client;

import ru.practicum.stats.dto.EndpointHitCreateDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void hit(EndpointHitCreateDto dto);

    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris,
                                boolean unique);
}
