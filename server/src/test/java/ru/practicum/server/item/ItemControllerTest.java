package ru.practicum.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemBookingsDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    void testCreateItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Дрель", "Описание", true, null);

        Mockito.when(itemService.create(Mockito.eq(1L), Mockito.any()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Новое имя", "Описание", true, null);

        Mockito.when(itemService.update(Mockito.eq(1L), Mockito.eq(1L), Mockito.any()))
                .thenReturn(dto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"));
    }

    @Test
    void testGetItem() throws Exception {
        ItemBookingsDto dto = new ItemBookingsDto(1L, "Дрель", "Опис", true, null, null, null, List.of());

        Mockito.when(itemService.getById(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetOwnerItems() throws Exception {
        Mockito.when(itemService.getOwnerItems(1L)).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchItems() throws Exception {
        Mockito.when(itemService.search("дрель")).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddComment() throws Exception {
        CommentDto request = new CommentDto(null, "Комментарий", null, null);
        CommentDto response = new CommentDto(1L, "Комментарий", "Автор", LocalDateTime.now());

        Mockito.when(itemService.addComment(Mockito.eq(1L), Mockito.eq(1L), Mockito.any()))
                .thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
