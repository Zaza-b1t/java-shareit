package ru.practicum.shareit.exception;

public class InternalServerErrorException extends AbstractDtoException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
