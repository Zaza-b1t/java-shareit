package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AbstractDtoException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
