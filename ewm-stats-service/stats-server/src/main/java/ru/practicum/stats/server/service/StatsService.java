package ru.practicum.stats.server.service;

import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitCreateDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.EndpointHit;
import ru.practicum.stats.server.exception.ConditionsNotMetException;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.server.exception.ExceptionMessages.BLANK_OR_NULL_START_DATE_OR_END_DATE;
import static ru.practicum.stats.server.exception.ExceptionMessages.START_IS_AFTER_END;

@Service
public class StatsService {
    private final StatsRepository repository;

    public StatsService(StatsRepository repository) {
        this.repository = repository;
    }

    public void save(EndpointHitCreateDto dto) {
        EndpointHit endpointHit = EndpointHitMapper.fromDto(dto);
        repository.save(endpointHit);
    }

    public List<ViewStatsDto> getStatistic(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           boolean unique) {
        if (start == null || end == null) {
            throw new ConditionsNotMetException(BLANK_OR_NULL_START_DATE_OR_END_DATE);
        }

        if (start.isAfter(end)) {
            throw new ConditionsNotMetException(START_IS_AFTER_END);
        }

        boolean hasUris = uris != null && !uris.isEmpty();

        if (unique) {
            return hasUris
                    ? repository.findStatsWhereUrisInWithUniqueIp(start, end, uris)
                    : repository.findStatsWithUniqueIp(start, end);
        }

        return hasUris
                ? repository.findStatsWhereUrisIn(start, end, uris)
                : repository.findStats(start, end);
    }
}
