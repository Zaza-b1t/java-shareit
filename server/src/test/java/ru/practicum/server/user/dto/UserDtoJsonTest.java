package ru.practicum.server.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "test", "test@mail.ru");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.ru");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonText = """
        {
          "id": 10,
          "name": "Bob",
          "email": "bob@mail.ru"
        }
        """;

        UserDto dto = json.parseObject(jsonText);

        assertThat(dto.getId()).isEqualTo(10);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@mail.ru");
    }
}
