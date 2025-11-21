package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
   private long id;

   private long itemId;

   private long bookerId;

   @NotNull
   @Future
   private LocalDateTime start;

   @NotNull
   @Future
   private LocalDateTime end;

   private BookingStatus status;
}
