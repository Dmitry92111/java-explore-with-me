package ru.practicum.stats.server.exception;

public class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String BLANK_OR_NULL_START_DATE_OR_END_DATE = "Parameters start and end cannot be null";
    public static final String START_IS_AFTER_END = "Parameter start cannot be after end";
}

