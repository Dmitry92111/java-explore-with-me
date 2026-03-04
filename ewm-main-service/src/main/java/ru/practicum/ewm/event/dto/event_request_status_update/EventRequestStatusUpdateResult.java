package ru.practicum.ewm.event.dto.event_request_status_update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.participation_request.dto.ParticipationRequestDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private final List<ParticipationRequestDto> confirmedRequests;
    private final List<ParticipationRequestDto> rejectedRequests;
}
