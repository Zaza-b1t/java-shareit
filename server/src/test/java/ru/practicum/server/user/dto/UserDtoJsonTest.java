package ru.practicum.server.user.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoJsonTest {

    @Test
    void testSerialize() {
        UserDto dto = new UserDto(1L, "test", "test@mail.ru");

        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getName());
        assertEquals("test@mail.ru", dto.getEmail());
    }

    @Test
    void testDeserialize() {
        UserDto dto = new UserDto(10L, "Bob", "bob@mail.ru");

        assertEquals(10L, dto.getId());
        assertEquals("Bob", dto.getName());
        assertEquals("bob@mail.ru", dto.getEmail());
    }
}
