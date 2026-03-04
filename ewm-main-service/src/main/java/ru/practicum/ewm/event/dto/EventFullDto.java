package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.event.location.LocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class EventFullDto {
    private final long id;
    private final String title;
    private final String annotation;
    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn;

    private final boolean paid;
    private final boolean requestModeration;

    private final int participantLimit;

    private final EventStatus state;
    private final LocationDto location;

    private final CategoryDto category;
    private final UserShortDto initiator;

    private final long confirmedRequests;
    private final long views;
}
