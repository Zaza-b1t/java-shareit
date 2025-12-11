package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemBookingsDto;
import ru.practicum.server.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    ItemBookingsDto getById(Long requesterId, Long itemId);

    List<ItemBookingsDto> getOwnerItems(Long ownerId);

    List<ItemBookingsDto> search(String text);

    void deleteById(Long itemId);

    void deleteAllByOwnerId(Long ownerId);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto);
}
