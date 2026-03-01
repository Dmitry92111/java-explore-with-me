package ru.practicum.ewm.error.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends BaseApplicationException {
    public NotFoundException(String message) {
        super(message);
    }
}
