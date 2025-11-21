package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(long id,User user);

    User getById(long id);

    Collection<User> getAll();

    boolean exists(long id);

    void delete(long id);
}
