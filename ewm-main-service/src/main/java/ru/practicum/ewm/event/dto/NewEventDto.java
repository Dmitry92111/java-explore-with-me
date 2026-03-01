package ru.practicum.ewm.event.dto;

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
public class NewEventDto {

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private boolean paid;
    private Boolean requestModeration;

    @Min(0)
    private int participantLimit;

    @NotNull
    @Valid
    private LocationDto location;

    @NotNull
    @Positive
    private Long categoryId;
}
