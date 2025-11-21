package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

public interface ItemRequestStorage {
    ItemRequest create(ItemRequest request);

    ItemRequest getById(long id);

    Collection<ItemRequest> getAll();

    Collection<ItemRequest> getByRequester(long requesterId);

    void delete(long id);
}
