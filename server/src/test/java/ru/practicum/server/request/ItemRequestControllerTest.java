package ru.practicum.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;


    @Test
    void testCreateRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                1L, "Нужен молоток", LocalDateTime.now(), List.of()
        );

        Mockito.when(requestService.create(Mockito.eq(1L), Mockito.any()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetOwnRequests() throws Exception {
        Mockito.when(requestService.getOwn(1L))
                .thenReturn(List.of());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOthers() throws Exception {
        Mockito.when(requestService.getAllOthers(1L))
                .thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                5L, "Описание", LocalDateTime.now(), List.of()
        );

        Mockito.when(requestService.getById(1L, 5L))
                .thenReturn(dto);

        mvc.perform(get("/requests/5")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}
