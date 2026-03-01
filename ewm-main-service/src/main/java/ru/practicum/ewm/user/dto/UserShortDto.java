package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserShortDto {
    private final long id;
    private final String name;
}
