package ru.practicum.ewm.event.dto.event_request_status_update;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<@Positive @UniqueElements Long> requestIds;
    private EventRequestStatus status;
}
