package ru.practicum.ewm.event.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.event.metrics.EventMetricsService;
import ru.practicum.ewm.event.util.EventUtils;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitCreateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicEventService {

    private final EventMapper eventMapper;
    private final EventMetricsService eventMetricsService;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<EventShortDto> findEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          boolean onlyAvailable,
                                          String sort,
                                          int from,
                                          int size) {

        LocalDateTime start = EventUtils.parseDateOrNull(rangeStart, "rangeStart");
        LocalDateTime end = EventUtils.parseDateOrNull(rangeEnd, "rangeEnd");

        if (start == null && end == null) {
            start = LocalDateTime.now();
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new ConditionsNotMetException(ExceptionMessages.START_DATE_AFTER_END_DATE);
        }

        PublicEventSort sortMode = parseSort(sort);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);

        root.fetch("initiator", JoinType.LEFT);
        root.fetch("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("state"), EventStatus.PUBLISHED));

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.trim().toLowerCase() + "%";
            Predicate inAnnotation = cb.like(cb.lower(root.get("annotation")), pattern);
            Predicate inDescription = cb.like(cb.lower(root.get("description")), pattern);
            predicates.add(cb.or(inAnnotation, inDescription));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }

        if (onlyAvailable) {
            Subquery<Long> confirmedSub = cq.subquery(Long.class);
            Root<ParticipationRequest> pr = confirmedSub.from(ParticipationRequest.class);

            confirmedSub.select(cb.count(pr))
                    .where(
                            cb.equal(pr.get("event").get("id"), root.get("id")),
                            cb.equal(pr.get("status"), ParticipationRequestStatus.CONFIRMED)
                    );

            Predicate unlimited = cb.equal(root.get("participantLimit"), 0);
            Predicate hasSpots = cb.greaterThan(root.get("participantLimit").as(Long.class), confirmedSub);

            predicates.add(cb.or(unlimited, hasSpots));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));


        if (sortMode == PublicEventSort.EVENT_DATE) {
            cq.orderBy(cb.asc(root.get("eventDate")));
            cq.distinct(true);

            TypedQuery<Event> query = entityManager.createQuery(cq);
            query.setFirstResult(from);
            query.setMaxResults(size);

            List<Event> events = query.getResultList();
            return mapToShortDtosWithMetrics(events);

        } else {
            cq.orderBy(cb.asc(root.get("eventDate")));
            cq.distinct(true);

            List<Event> all = entityManager.createQuery(cq).getResultList();


            Map<Long, Long> confirmed = eventMetricsService.getConfirmedRequestsForEvents(all);
            Map<Long, Long> views = eventMetricsService.getViewsStatsForEvents(all);


            all.sort((a, b) -> Long.compare(
                    views.getOrDefault(b.getId(), 0L),
                    views.getOrDefault(a.getId(), 0L)
            ));

            int startIdx = Math.min(from, all.size());
            int endIdx = Math.min(from + size, all.size());
            List<Event> page = all.subList(startIdx, endIdx);

            return page.stream()
                    .map(event -> eventMapper.toEventShortDto(
                            event,
                            confirmed.getOrDefault(event.getId(), 0L),
                            views.getOrDefault(event.getId(), 0L)
                    ))
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public EventFullDto findEventById(long eventId) {
        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId).orElseThrow(
                () -> new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId)));
        long confirmedRequests = eventMetricsService.getConfirmedRequestsForEvent(event);
        long views = eventMetricsService.getViewsStatsForEvent(event);
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    private List<EventShortDto> mapToShortDtosWithMetrics(List<Event> events) {
        if (events.isEmpty()) return List.of();

        Map<Long, Long> confirmed = eventMetricsService.getConfirmedRequestsForEvents(events);
        Map<Long, Long> views = eventMetricsService.getViewsStatsForEvents(events);

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(
                        e,
                        confirmed.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)
                ))
                .toList();
    }


    private PublicEventSort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return PublicEventSort.EVENT_DATE; // дефолт обычно по дате
        try {
            return PublicEventSort.valueOf(sort.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ConditionsNotMetException("Unknown sort: " + sort + " (allowed: EVENT_DATE, VIEWS)");
        }
    }

    public void saveHit(HttpServletRequest request) {
        EndpointHitCreateDto dto = new EndpointHitCreateDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.hit(dto);
    }
}
