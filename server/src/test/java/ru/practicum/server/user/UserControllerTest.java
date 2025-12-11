package ru.practicum.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;


    @Test
    void testCreateUser() throws Exception {
        UserDto dto = new UserDto(1L, "new", "new@mail.ru");

        Mockito.when(userService.create(Mockito.any())).thenReturn(dto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto dto = new UserDto(1L, "upd", "u@mail.ru");

        Mockito.when(userService.update(Mockito.eq(1L), Mockito.any()))
                .thenReturn(dto);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("upd"));
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto dto = new UserDto(5L, "name", "mail@mail.ru");

        Mockito.when(userService.getById(5L)).thenReturn(dto);

        mvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void testGetAllUsers() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/2"))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(2L);
    }
}
