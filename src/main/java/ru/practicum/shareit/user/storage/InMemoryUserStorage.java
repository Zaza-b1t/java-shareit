package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        if (!exists(id)) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User getById(long id) {
        if (!exists(id)) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public boolean exists(long id) {
        return users.containsKey(id);
    }

    @Override
    public void delete(long id) {
        if (!exists(id)) {
            throw new EntityNotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        users.remove(id);
    }

    private long getNextId() {
        return ++currentId;
    }
}
