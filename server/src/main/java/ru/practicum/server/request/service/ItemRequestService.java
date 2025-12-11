package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAllOthers(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
