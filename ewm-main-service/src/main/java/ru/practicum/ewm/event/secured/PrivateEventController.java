package ru.practicum.ewm.event.secured;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.event_request_status_update.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.event_request_status_update.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.update_event_user_request.UpdateEventUserRequest;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody NewEventDto createDto) {
        return privateEventService.create(userId, createDto);
    }

    @GetMapping
    public List<EventShortDto> findAllEventsOfUser(@PathVariable long userId,
                                                   @Min(0) @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        return privateEventService.findAllEventsOfUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventOfUserById(@PathVariable long userId,
                                            @PathVariable long eventId) {
        return privateEventService.findEventOfUserById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventUserRequest dto) {
        return privateEventService.update(dto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsOfEvent(@PathVariable long userId,
                                                               @PathVariable long eventId) {
        return privateEventService.getAllRequestsOfEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeEventParticipationRequestsStatus(@PathVariable long userId,
                                                                                 @PathVariable long eventId,
                                                                                 @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        return privateEventService.changeEventParticipationRequestsStatus(userId, eventId, dto);
    }
}
