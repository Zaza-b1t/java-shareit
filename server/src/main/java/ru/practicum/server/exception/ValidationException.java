package ru.practicum.server.exception;

public class ValidationException extends AbstractDtoException {
    public ValidationException(String message) {
        super(message);
    }
}
