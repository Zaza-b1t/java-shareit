package ru.practicum.shareit.exception;

public class EntityNotFoundException extends AbstractDtoException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
