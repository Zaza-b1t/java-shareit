package ru.practicum.server.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoJsonTest {

    @Test
    void testSerialize() {
        ItemDto dto = new ItemDto(1L, "Дрель", "Описание", true, null);

        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertTrue(dto.getAvailable());
    }

    @Test
    void testDeserialize() {
        ItemDto dto = new ItemDto(1L, "Дрель", "Описание", true, null);

        assertEquals(1, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertTrue(dto.getAvailable());
    }
}
