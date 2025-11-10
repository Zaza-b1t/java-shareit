package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long ownerId, ItemDto itemDto);

    ItemDto update(long ownerId, long itemId, ItemDto itemDto);

    ItemDto getById(long id);

    Collection<ItemDto> getByOwner (long id);

    void delete(long ownerId, long id);

    Collection<ItemDto> search(String text);

    ItemDto patch(long ownerId, long itemId, ItemDto dto);

    boolean existsById(long id);
}
