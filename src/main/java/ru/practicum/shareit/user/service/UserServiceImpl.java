package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Создание пользователя с email={}", userDto.getEmail());
        for (User existing : userStorage.getAll()) {
            if (existing.getEmail().equalsIgnoreCase(userDto.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует.");
            }
        }
        User toSave = userMapper.toUser(userDto);
        User saved = userStorage.create(toSave);
        log.info("Создан пользователь id={} email={}", saved.getId(), saved.getEmail());
        return userMapper.toUserDto(saved);
    }

    @Override
    public UserDto patch(long id, UserDto dto) {
        log.info("Частичное обновление пользователя id={}", id);
        User existing = userStorage.getById(id);

        if (dto.getEmail() != null) {
            for (User other : userStorage.getAll()) {
                if (other.getEmail().equalsIgnoreCase(dto.getEmail()) && other.getId() != id) {
                    throw new DuplicateEmailException("Email уже используется другим пользователем.");
                }
            }
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        User updated = userStorage.update(id, existing);
        log.info("Пользователь id={} обновлён", id);
        return userMapper.toUserDto(updated);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        log.info("Полное обновление пользователя id={}", id);
        User toUpdate = userMapper.toUser(userDto);
        toUpdate.setId(id);
        User updated = userStorage.update(id, toUpdate);
        log.info("Пользователь id={} полностью обновлён", id);
        return userMapper.toUserDto(updated);
    }

    @Override
    public UserDto getById(long id) {
        log.info("Получение пользователя по id={}", id);
        User user = userStorage.getById(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("Получение списка всех пользователей");
        Collection<User> users = userStorage.getAll();
        if (users.isEmpty()) {
            log.warn("Список пользователей пуст");
            return List.of();
        }
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public boolean exists(long id) {
        return userStorage.exists(id);
    }

    @Override
    public void delete(long id) {
        log.info("Удаление пользователя id={}", id);
        userStorage.delete(id);
        log.info("Пользователь id={} удалён", id);
    }
}

