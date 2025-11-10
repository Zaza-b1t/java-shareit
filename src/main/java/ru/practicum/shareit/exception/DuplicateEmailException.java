package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends AbstractDtoException {
    public DuplicateEmailException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}