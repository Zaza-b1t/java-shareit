package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item update(long id, Item item);

    Item getById(long id);

    Collection<Item> getByOwner(long id);

    void delete(long itemId);

    boolean exists(long id);

    Collection<Item> search(String text);
}
