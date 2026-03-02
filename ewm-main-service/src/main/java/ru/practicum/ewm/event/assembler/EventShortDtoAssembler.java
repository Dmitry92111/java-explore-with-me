package ru.practicum.ewm.event.assembler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.metrics.EventMetricsService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventShortDtoAssembler {

    private final EventMetricsService eventMetricsService;
    private final EventMapper eventMapper;

    public List<EventShortDto> toEventShortDtos(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyList();

        Map<Long, Long> confirmed = eventMetricsService.getConfirmedRequestsForEvents(events);
        Map<Long, Long> views = eventMetricsService.getViewsStatsForEvents(events);

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(
                        e,
                        confirmed.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)
                )).toList();
    }
}
