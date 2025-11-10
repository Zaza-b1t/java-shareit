package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatusCode;

public record ErrorResponse(String error, HttpStatusCode httpStatusCode) {
}
