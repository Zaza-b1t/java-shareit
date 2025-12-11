package ru.practicum.server.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.server.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 1, 13, 0);

        BookingDto dto = new BookingDto(
                1L, start, end,
                5L, 7L,
                BookingStatus.APPROVED,
                null, null
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = """
{
    "id": 1,
    "start": "2030-01-01T12:00:00",
    "end": "2030-01-01T13:00:00",
    "itemId": 5,
    "bookerId": 7,
    "status": "WAITING"
}
""";

        BookingDto dto = json.parseObject(jsonString);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getItemId()).isEqualTo(5L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}
