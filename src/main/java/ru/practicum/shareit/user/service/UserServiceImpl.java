package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Создание пользователя с email={}", userDto.getEmail());
            if (repository.existsByEmailIgnoreCase(userDto.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует.");
            }
        User toSave = UserMapper.toUser(userDto);
        User saved = repository.save(toSave);
        log.info("Создан пользователь id={} email={}", saved.getId(), saved.getEmail());
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto getById(long id) {
        log.info("Получение пользователя по id={}", id);
        User user = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("Получение списка всех пользователей");
        Collection<User> users = repository.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        log.info("Обновление пользователя id={}", id);
        if (!exists(id)) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
        User toUpdate = UserMapper.toUser(userDto);
        toUpdate.setId(id);
        User updated = repository.save(toUpdate);
        log.info("Пользователь id={} полностью обновлён", id);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public void delete(long id) {
        log.info("Удаление пользователя id={}", id);
        if (!exists(id)) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
        repository.deleteById(id);
        log.info("Пользователь id={} удалён", id);
    }

    @Override
    public UserDto patch(long id, UserDto dto) {
        log.info("Частичное обновление пользователя id={}", id);

        User existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

        if (dto.getEmail() != null) {
            String newEmail = dto.getEmail();

            if (newEmail != null && !newEmail.equalsIgnoreCase(existing.getEmail())) {
                boolean emailTaken = repository.existsByEmailIgnoreCaseAndIdNot(newEmail, existing.getId());
                if (emailTaken) {
                    throw new InternalServerErrorException("Пользователь с таким email уже существует");
                }
                existing.setEmail(newEmail);
            }
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        User updated = repository.save(existing);
        log.info("Пользователь id={} обновлён", id);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public boolean exists(long id) {
        return repository.existsById(id);
    }
}

