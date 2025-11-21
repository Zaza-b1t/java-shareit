package ru.practicum.shareit.exception;

public abstract class AbstractDtoException extends RuntimeException {

    public AbstractDtoException(String message) {
        super(message);
    }
}
