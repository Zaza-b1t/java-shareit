package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AbstractDtoException {
    public ValidationException(String message) {
        super(message);
    }
}
