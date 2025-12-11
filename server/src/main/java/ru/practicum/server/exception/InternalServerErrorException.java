package ru.practicum.server.exception;

public class InternalServerErrorException extends AbstractDtoException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
