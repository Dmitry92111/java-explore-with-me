package ru.practicum.ewm.event.secured;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.event_request_status_update.EventRequestStatus;
import ru.practicum.ewm.event.dto.event_request_status_update.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.event_request_status_update.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.update_event_user_request.UpdateEventUserRequest;
import ru.practicum.ewm.event.location.LocationMapper;
import ru.practicum.ewm.event.metrics.EventMetricsService;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.participation_request.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;


import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMetricsService eventMetricsService;
    private final ParticipationRequestRepository participationRequestRepository;

    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional
    public EventFullDto create(long userId, NewEventDto createDto) {
        User user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId))
        );

        Category category = findCategoryOrThrow404(createDto.getCategoryId());

        validateEventDateForCreateAndUpdateEvent(createDto.getEventDate());

        Event event = eventMapper.fromNewEventDto(createDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStatus.PENDING);
        event.setInitiator(user);
        event.setCategory(category);

        event.setRequestModeration(createDto.getRequestModeration() == null ? true : createDto.getRequestModeration());

        Event saved = eventRepository.save(event);
        return eventMapper.toEventFullDtoForCreateAndUpdate(saved);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> findAllEventsOfUser(long userId, int from, int size) {
        checkUserExists(userId);

        List<Long> eventIds = eventRepository.findEventIdsByInitiatorId(userId, from, size);
        List<Event> events = eventRepository.findAllWithCategoryAndInitiatorByIdIn(eventIds);
        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> confirmedRequestsByEventId = eventMetricsService.getConfirmedRequestsForEvents(events);
        Map<Long, Long> viewsById = eventMetricsService.getViewsStatsForEvents(events);

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        confirmedRequestsByEventId.getOrDefault(event.getId(), 0L),
                        viewsById.getOrDefault(event.getId(), 0L)
                )).toList();
    }

    @Transactional(readOnly = true)
    public EventFullDto findEventOfUserById(long userId, long eventId) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId)));
        long confirmedRequests = eventMetricsService.getConfirmedRequestsForEvent(event);
        long views = eventMetricsService.getViewsStatsForEvent(event);
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Transactional
    public EventFullDto update(UpdateEventUserRequest dto, long userId, long eventId) {
        Event event = eventRepository
                .findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId)));

        if (event.getState() == EventStatus.PUBLISHED) {
            throw new ConditionsNotMetException(ExceptionMessages.SHOULD_BE_IMPOSSIBLE_TO_UPDATE_PUBLISHED_EVENT);
        }

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
            validateEventDateForCreateAndUpdateEvent(dto.getEventDate());
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
                case SEND_TO_REVIEW -> {
                    if (event.getState() == EventStatus.PENDING) throw new ConditionsNotMetException(
                            ExceptionMessages.SHOULD_BE_IMPOSSIBLE_TO_SEND_PENDING_REQUEST_TO_REVIEW
                    );
                    event.setState(EventStatus.PENDING);
                }
                case CANCEL_REVIEW -> {
                    if (event.getState() == EventStatus.CANCELED) throw new ConditionsNotMetException(
                            ExceptionMessages.SHOULD_BE_IMPOSSIBLE_TO_CANCEL_CANCELLED_REQUEST
                    );
                    event.setState(EventStatus.CANCELED);
                }
            }
        }
        return eventMapper.toEventFullDtoForCreateAndUpdate(event);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequestsOfEvent(long userId, long eventId) {
        eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.EVENT_NOT_FOUND));
        List<ParticipationRequest> requests = participationRequestRepository.findAllByEvent_Id(eventId);
        if (requests.isEmpty()) {
            return List.of();
        }
        return participationRequestMapper.toParticipationRequestDtos(requests);
    }

    @Transactional
    public EventRequestStatusUpdateResult changeEventParticipationRequestsStatus(long userId,
                                                                                 long eventId,
                                                                                 EventRequestStatusUpdateRequest dto) {
        Event event = eventRepository.findByIdAndInitiatorIdForUpdate(eventId, userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.EVENT_NOT_FOUND));

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ConditionsNotMetException(ExceptionMessages.CONFIRMATION_OF_PARTICIPATION_REQUEST_IS_NOT_REQUIRED);
        }

        List<Long> ids = dto.getRequestIds();
        List<ParticipationRequest> allByIds = participationRequestRepository.findAllByIdIn(ids);
        if (allByIds.size() != ids.size()) {
            throw new NotFoundException(ExceptionMessages.SOME_PARTICIPATION_REQUEST_NOT_FOUND);
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdInAndEvent_Id(ids, eventId);
        if (requests.size() != ids.size()) {
            throw new ConditionsNotMetException(ExceptionMessages.SOME_REQUESTS_DO_NOT_MATCH_WITH_PROVIDED_EVENT_ID);
        }

        boolean allPending = requests.stream().allMatch(r -> r.getStatus() == ParticipationRequestStatus.PENDING);
        if (!allPending) {
            throw new ConditionsNotMetException(ExceptionMessages.YOU_CAN_CHANGE_STATUS_PENDING_PARTICIPATION_REQUESTS_ONLY);
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        if (dto.getStatus() == EventRequestStatus.REJECTED) {
            requests.forEach(r -> {
                r.setStatus(ParticipationRequestStatus.REJECTED);
                rejected.add(r);
            });
        } else {
            long confirmedCount = participationRequestRepository.countByEvent_IdAndStatus(
                    event.getId(),
                    ParticipationRequestStatus.CONFIRMED);
            long available = event.getParticipantLimit() - confirmedCount;
            if (available <= 0) {
                throw new ConditionsNotMetException(
                        ExceptionMessages.CANNOT_CREATE_OR_CONFIRM_PARTICIPATION_REQUEST_WHEN_REQUEST_LIMIT_HAS_BEEN_REACHED);
            }

            for (ParticipationRequest r : requests) {
                if (available > 0) {
                    r.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmed.add(r);
                    available--;
                } else {
                    r.setStatus(ParticipationRequestStatus.REJECTED);
                    rejected.add(r);
                }
            }

            if (available == 0) {
                List<ParticipationRequest> restPending = participationRequestRepository.findAllByEvent_IdAndStatus(
                        eventId,
                        ParticipationRequestStatus.PENDING);

                restPending.forEach(r -> r.setStatus(ParticipationRequestStatus.REJECTED));
            }
        }
        return new EventRequestStatusUpdateResult(
                participationRequestMapper.toParticipationRequestDtos(confirmed),
                participationRequestMapper.toParticipationRequestDtos(rejected));
    }


    private void checkUserExists(long userId) {
        if (!userRepository.existsByIdAndDeletedIsFalse(userId)) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId));
        }
    }

    private Category findCategoryOrThrow404(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.CATEGORY_NOT_FOUND, categoryId)));
    }

    private void validateEventDateForCreateAndUpdateEvent(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConditionsNotMetException(String.format
                    (ExceptionMessages.DEFAULT_FIELD_S_ERROR_S_VALUE_S_MESSAGE,
                            "eventDate", "должно содержать дату, которая еще не наступила.", eventDate));
        }
    }
}
