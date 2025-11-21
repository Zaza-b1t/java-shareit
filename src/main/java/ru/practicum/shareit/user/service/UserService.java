package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto patch(long id, UserDto dto);

    UserDto update(long id, UserDto userDto);

    UserDto getById(long id);

    Collection<UserDto> getAll();

    boolean exists(long id);

    void delete(long id);
}
