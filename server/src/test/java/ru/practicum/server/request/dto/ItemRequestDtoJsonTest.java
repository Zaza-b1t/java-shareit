package ru.practicum.server.request.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoJsonTest {

    @Test
    void testSerialize() {
        LocalDateTime created = LocalDateTime.of(2030, 1, 1, 12, 0);

        ItemRequestItemDto item = new ItemRequestItemDto(10L, "Дрель", 99L);

        ItemRequestDto dto = new ItemRequestDto(
                1L, "Описание", created, List.of(item)
        );

        assertEquals(1L, dto.getId());
        assertEquals("Описание", dto.getDescription());
        assertEquals(created, dto.getCreated());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testDeserialize() {
        LocalDateTime created = LocalDateTime.of(2030, 1, 1, 12, 0);

        ItemRequestDto dto = new ItemRequestDto(
                2L, "Тест", created,
                List.of(new ItemRequestItemDto(10L, "Вещь", 3L))
        );

        assertEquals(2L, dto.getId());
        assertEquals("Тест", dto.getDescription());
        assertEquals(1, dto.getItems().size());
    }
}
