package ru.practicum.ewm.participation_request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ParticipationRequestDto {
    private final long id;
    private final ParticipationRequestStatus status;

    private final LocalDateTime created;

    private final long event;
    private final long requester;
}
