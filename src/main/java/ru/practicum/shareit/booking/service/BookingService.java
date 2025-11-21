package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(long bookerId, BookingDto dto);

    BookingDto updateStatus(long ownerId, long bookingId, boolean approved);

    BookingDto getById(long userId, long bookingId);

    Collection<BookingDto> getByBooker(long bookerId);

    Collection<BookingDto> getByOwner(long ownerId);
}