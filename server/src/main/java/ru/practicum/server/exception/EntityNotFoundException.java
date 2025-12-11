package ru.practicum.server.exception;

public class EntityNotFoundException extends AbstractDtoException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
