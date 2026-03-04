package ru.practicum.ewm.participation_request.secured;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateParticipationRequestController {
    private final PrivateParticipationRequestService privateParticipationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @Positive @RequestParam long eventId) {
        return privateParticipationRequestService.create(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> findAllRequestsOfUser(@PathVariable long userId) {
        return privateParticipationRequestService.findAllRequestsOfUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        return privateParticipationRequestService.cancelRequest(userId, requestId);
    }
}
