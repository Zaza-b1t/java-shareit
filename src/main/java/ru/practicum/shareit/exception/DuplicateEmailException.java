package ru.practicum.shareit.exception;

public class DuplicateEmailException extends AbstractDtoException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}