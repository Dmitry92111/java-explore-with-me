package ru.practicum.ewm.error.exception;

public class BadRequestException extends BaseApplicationException {
    public BadRequestException(String message) {
        super(message);
    }
}
