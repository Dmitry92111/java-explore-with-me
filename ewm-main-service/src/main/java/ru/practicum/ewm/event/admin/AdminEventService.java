package ru.practicum.ewm.event.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.update_event_admin_request.UpdateEventAdminRequest;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.event.location.LocationMapper;
import ru.practicum.ewm.event.metrics.EventMetricsService;
import ru.practicum.ewm.event.util.EventUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final EventMetricsService eventMetricsService;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationMapper locationMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<EventFullDto> findEvents(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         String rangeStart,
                                         String rangeEnd,
                                         int from,
                                         int size) {

        LocalDateTime start = EventUtils.parseDateOrNull(rangeStart, "rangeStart");
        LocalDateTime end = EventUtils.parseDateOrNull(rangeEnd, "rangeEnd");

        if (start != null && end != null && start.isAfter(end)) {
            throw new ConditionsNotMetException(ExceptionMessages.START_DATE_AFTER_END_DATE);
        }

        List<EventStatus> parsedStates = EventUtils.parseStates(states);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(users));
        }

        if (!parsedStates.isEmpty()) {
            predicates.add(root.get("state").in(parsedStates));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }

        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }

        root.fetch("initiator", JoinType.LEFT);
        root.fetch("category", JoinType.LEFT);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(root.get("eventDate")));

        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<Event> events = typedQuery.getResultList();

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> confirmedRequestsByEventId = eventMetricsService.getConfirmedRequestsForEvents(events);
        Map<Long, Long> viewsById = eventMetricsService.getViewsStatsForEvents(events);

        return events.stream()
                .map(event -> eventMapper.toEventFullDto(
                        event,
                        confirmedRequestsByEventId.getOrDefault(event.getId(), 0L),
                        viewsById.getOrDefault(event.getId(), 0L)
                )).toList();
    }

    @Transactional
    public EventFullDto update(long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.EVENT_NOT_FOUND));

        if (dto.getCategoryId() != null) {
            Category category = findCategoryOrThrow404(dto.getCategoryId());
            event.setCategory(category);
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            validateEventDateForUpdateEvent(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationMapper.toEntity(dto.getLocation()));
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventStatus.PENDING) throw new ConditionsNotMetException(
                            ExceptionMessages.ONLY_PENDING_EVENT_CAN_BE_PUBLISHED
                    );
                    event.setState(EventStatus.PUBLISHED);
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventStatus.PUBLISHED) throw new ConditionsNotMetException(
                            ExceptionMessages.SHOULD_BE_IMPOSSIBLE_TO_REJECT_PUBLISHED_EVENT
                    );
                    event.setState(EventStatus.CANCELED);
                }
            }
        }
        return eventMapper.toEventFullDtoForCreateAndUpdate(event);
    }


    private Category findCategoryOrThrow404(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.CATEGORY_NOT_FOUND, categoryId)));
    }

    private void validateEventDateForUpdateEvent(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConditionsNotMetException(String.format
                    (ExceptionMessages.DEFAULT_FIELD_S_ERROR_S_VALUE_S_MESSAGE,
                            "eventDate", "Too early to change this event", eventDate));
        }
    }
}