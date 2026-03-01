package ru.practicum.ewm.event.metrics;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.participation_request.EventCountView;
import ru.practicum.ewm.participation_request.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventMetricsService {
    private final StatsClient statsClient;

    private final ParticipationRequestRepository participationRequestRepository;

    public Map<Long, Long> getConfirmedRequestsForEvents(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .distinct()
                .toList();

        if (eventIds.isEmpty()) return Collections.emptyMap();

        return participationRequestRepository
                .countByEventIdsAndStatus(eventIds, ParticipationRequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(EventCountView::getEventId, EventCountView::getCount));
    }

    public Map<Long, Long> getViewsStatsForEvents(List<Event> events) {
        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getState() == EventStatus.PUBLISHED
                        && event.getPublishedOn() != null).toList();

        if (publishedEvents.isEmpty()) return Collections.emptyMap();

        Map<String, Long> uriToId = publishedEvents.stream()
                .collect(Collectors.toMap(
                        event -> "/events/" + event.getId(),
                        Event::getId
                ));

        Map<Long, Long> viewsById = new HashMap<>();

        LocalDateTime minPublishedDate = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        List<String> uris = new ArrayList<>(uriToId.keySet());

        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(minPublishedDate, now, uris, false);

        viewStatsDtos.forEach(viewStatsDto -> {
            Long eventId = uriToId.get(viewStatsDto.getUri());
            if (eventId != null) {
                viewsById.put(eventId, viewStatsDto.getHits());
            }
        });

        return viewsById;
    }

    public long getViewsStatsForEvent(Event event) {
        return getViewsStatsForEvents(List.of(event)).getOrDefault(event.getId(), 0L);
    }

    public long getConfirmedRequestsForEvent(Event event) {
        return getConfirmedRequestsForEvents(List.of(event)).getOrDefault(event.getId(), 0L);
    }
}
