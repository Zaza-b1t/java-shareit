package ru.practicum.server.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.of(2030, 1, 1, 12, 0);

        CommentDto dto = new CommentDto(1L, "text", "author", now);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonText = """
{
    "id": 1,
    "text": "comment",
    "authorName": "user",
    "created": "2030-01-01T12:00:00"
}
""";

        CommentDto dto = json.parseObject(jsonText);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("comment");
        assertThat(dto.getAuthorName()).isEqualTo("user");
    }
}
