package ru.practicum.ewm.event.dto.update_event_user_request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.location.LocationDto;
import ru.practicum.ewm.json_utils.TrimStringDeserializer;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventUserRequest {

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @Size(min = 3, max = 120)
    private String title;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @Size(min = 20, max = 2000)
    private String annotation;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Boolean paid;
    private Boolean requestModeration;

    @Min(0)
    private Integer participantLimit;

    @Valid
    private LocationDto location;

    @Positive
    private Long categoryId;

    private StateAction stateAction;
}
