package ru.practicum.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
   private Long id;

   @NotNull
   @Future
   private LocalDateTime start;

   @NotNull
   @Future
   private LocalDateTime end;

   @NotNull
   private Long itemId;

   private Long bookerId;

   private BookingStatus status;

   @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   private ItemDto item;

   @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   private UserDto booker;
}
