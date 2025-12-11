package ru.practicum.server.exception;

public abstract class AbstractDtoException extends RuntimeException {

    public AbstractDtoException(String message) {
        super(message);
    }
}
