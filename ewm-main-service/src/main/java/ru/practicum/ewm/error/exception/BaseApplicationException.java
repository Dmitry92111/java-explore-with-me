package ru.practicum.ewm.error.exception;

import lombok.Getter;

@Getter
public abstract class BaseApplicationException extends RuntimeException {
    protected BaseApplicationException(String message) {
        super(message);
    }
}
