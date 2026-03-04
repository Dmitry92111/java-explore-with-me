package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class EventShortDto {
    private final long id;
    private final String title;
    private final String annotation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final boolean paid;

    private final CategoryDto category;
    private final UserShortDto initiator;

    private final long confirmedRequests;
    private final long views;
}
