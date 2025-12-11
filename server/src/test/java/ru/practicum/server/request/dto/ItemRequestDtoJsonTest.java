package ru.practicum.server.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime time = LocalDateTime.of(2030, 1, 1, 12, 0);

        ItemRequestItemDto item = new ItemRequestItemDto(10L, "Дрель", 99L);

        ItemRequestDto dto = new ItemRequestDto(
                1L, "Описание", time, List.of(item)
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2030-01-01T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(99);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonText = """
{
    "id": 2,
    "description": "Тест",
    "created": "2030-01-01T12:00:00",
    "items": [
        { "id": 10, "name": "Вещь", "ownerId": 3 }
    ]
}
""";

        ItemRequestDto dto = json.parseObject(jsonText);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getDescription()).isEqualTo("Тест");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2030, 1, 1, 12, 0));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Вещь");
    }
}
