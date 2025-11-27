package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

   private Long id;

   private BookingStatus status;

   private UserDto booker;
   private ItemDto item;

   private Long itemId;

   @NotNull
   @Future
   private LocalDateTime start;

   @NotNull
   @Future
   private LocalDateTime end;
}
