package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long bookerId, BookingDto bookingDto);

    BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getBookerBookings(Long bookerId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);
}
