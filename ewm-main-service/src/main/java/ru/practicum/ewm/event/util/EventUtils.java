package ru.practicum.ewm.event.util;

import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.entity.EventStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventUtils {
    private EventUtils() {
    }

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseDateOrNull(String value, String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value, DTF);
        } catch (DateTimeParseException ex) {
            throw new ConditionsNotMetException(
                    String.format(ExceptionMessages.DEFAULT_FIELD_S_ERROR_S_VALUE_S_MESSAGE,
                            fieldName,
                            "Parsing Error",
                            value)
            );
        }
    }

    public static List<EventStatus> parseStates(List<String> states) {
        if (states == null || states.isEmpty()) return List.of();
        try {
            return states.stream()
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(EventStatus::valueOf)
                    .toList();
        } catch (IllegalArgumentException ex) {
            throw new ConditionsNotMetException(String.format(ExceptionMessages.UNKNOWN_EVENT_STATE, states));
        }
    }
}
