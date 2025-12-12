package ru.practicum.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
   private LocalDateTime start;
   private LocalDateTime end;
   private Long itemId;
   private Long bookerId;
   private BookingStatus status;

   @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   private ItemDto item;

   @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   private UserDto booker;
}
