package ru.practicum.server.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User saved;

    @BeforeEach
    void setup() {
        saved = userRepository.save(new User(null, "old", "old@mail.ru"));
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "new", "new@mail.ru");

        UserDto created = userService.create(dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("new");
    }

    @Test
    void testUpdateUser() {
        UserDto patch = new UserDto(null, "updated", "updated@mail.ru");

        UserDto updated = userService.update(saved.getId(), patch);

        assertThat(updated.getName()).isEqualTo("updated");
        assertThat(updated.getEmail()).isEqualTo("updated@mail.ru");
    }

    @Test
    void testGetUserById() {
        UserDto user = userService.getById(saved.getId());

        assertThat(user.getId()).isEqualTo(saved.getId());
        assertThat(user.getEmail()).isEqualTo("old@mail.ru");
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> list = userService.getAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    void testDeleteUser() {
        userService.delete(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}
