package ru.practicum.server.exception;

public class DuplicateEmailException extends AbstractDtoException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}