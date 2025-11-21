package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(long requesterId, ItemRequestDto dto);

    ItemRequestDto getById(long requestId);

    Collection<ItemRequestDto> getAll();

    Collection<ItemRequestDto> getByRequester(long requesterId);

    void delete(long requestId);
}
