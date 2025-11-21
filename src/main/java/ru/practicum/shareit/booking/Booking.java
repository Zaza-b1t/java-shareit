package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private long id;

    private long itemId;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
