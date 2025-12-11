package ru.practicum.server.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoJsonTest {

    @Test
    void testSerialize() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 1, 13, 0);

        BookingDto dto = new BookingDto(
                1L, start, end,
                5L, 7L,
                BookingStatus.APPROVED,
                null, null
        );

        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getItemId());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
    }

    @Test
    void testDeserialize() {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 12, 0),
                LocalDateTime.of(2030, 1, 1, 13, 0),
                5L,
                7L,
                BookingStatus.WAITING,
                null,
                null
        );

        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getItemId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }
}
