package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@AllArgsConstructor
@Getter
public class CompilationDto {
    private final long id;
    private final String title;
    private final boolean pinned;
    private final Set<EventShortDto> events;
}
