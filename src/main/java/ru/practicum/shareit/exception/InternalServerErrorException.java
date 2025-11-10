package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends AbstractDtoException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
