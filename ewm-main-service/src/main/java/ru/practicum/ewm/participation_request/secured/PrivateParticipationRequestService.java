package ru.practicum.ewm.participation_request.secured;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional
    public ParticipationRequestDto create(long requesterId, long eventId) {
        User requester = findUserOrThrow404(requesterId);
        Event event = findEventForUpdateOrThrow404(eventId);

        Long initiatorId = event.getInitiator().getId();
        if (initiatorId != null && initiatorId == requesterId) {
            throw new ConditionsNotMetException(String.format(
                    ExceptionMessages.INITIATOR_CANNOT_CREATE_REQUEST_TO_HIS_OWN_EVENT, eventId));
        }
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ConditionsNotMetException(
                    ExceptionMessages.SHOULD_BE_IMPOSSIBLE_TO_CREATE_PARTICIPATION_REQUEST_TO_NOT_PUBLISHED_EVENT);
        }

        if (event.getParticipantLimit() > 0) {
            long confirmedRequestOfEvent = participationRequestRepository.countByEvent_IdAndStatus(
                    event.getId(),
                    ParticipationRequestStatus.CONFIRMED);

            if (confirmedRequestOfEvent >= event.getParticipantLimit()) {
                throw new ConditionsNotMetException(
                        ExceptionMessages.CANNOT_CREATE_OR_CONFIRM_PARTICIPATION_REQUEST_WHEN_REQUEST_LIMIT_HAS_BEEN_REACHED);
            }
        }

        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setRequester(requester);
        newRequest.setEvent(event);
        newRequest.setCreated(LocalDateTime.now());

        ParticipationRequestStatus status =
                (event.getParticipantLimit() == 0 || !event.isRequestModeration())
                        ? ParticipationRequestStatus.CONFIRMED
                        : ParticipationRequestStatus.PENDING;

        newRequest.setStatus(status);

        ParticipationRequest saved = participationRequestRepository.save(newRequest);
        return participationRequestMapper.toParticipationRequestDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllRequestsOfUser(long userId) {
        checkUserExists(userId);
        return participationRequestMapper.toParticipationRequestDtos(
                participationRequestRepository.findAllByRequester_Id(userId));
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest request = participationRequestRepository.findByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ExceptionMessages.PARTICIPATION_REQUEST_NOT_FOUND, requestId)));
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return participationRequestMapper.toParticipationRequestDto(request);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsByIdAndDeletedIsFalse(userId)) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId));
        }
    }

    private User findUserOrThrow404(long userId) {
        return userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
                () -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId)));

    }

    private Event findEventForUpdateOrThrow404(long eventId) {
        return eventRepository.findByIdForUpdate(eventId).orElseThrow(
                () -> new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId)));
    }
}
