package ru.practicum.server.item.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoJsonTest {

    @Test
    void testSerialize() {
        LocalDateTime created = LocalDateTime.of(2030, 1, 1, 12, 0);
        CommentDto dto = new CommentDto(1L, "text", "author", created);

        assertEquals(1L, dto.getId());
        assertEquals("text", dto.getText());
        assertEquals("author", dto.getAuthorName());
    }

    @Test
    void testDeserialize() {
        CommentDto dto = new CommentDto(1L, "comment", "user",
                LocalDateTime.of(2030, 1, 1, 12, 0));

        assertEquals(1L, dto.getId());
        assertEquals("comment", dto.getText());
        assertEquals("user", dto.getAuthorName());
    }
}
