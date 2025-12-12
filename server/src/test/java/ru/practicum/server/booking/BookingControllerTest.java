package ru.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void testCreateBooking() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                5L,
                3L,
                BookingStatus.WAITING,
                null,
                null
        );

        Mockito.when(bookingService.create(Mockito.eq(3L), Mockito.any()))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBookingById() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                10L,
                5L,
                BookingStatus.WAITING,
                null,
                null
        );

        Mockito.when(bookingService.getById(5L, 1L)).thenReturn(dto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBookerBookings() throws Exception {
        Mockito.when(bookingService.getBookerBookings(3L, "ALL"))
                .thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 3L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }
}
