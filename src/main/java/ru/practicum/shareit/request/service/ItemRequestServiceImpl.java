package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;

import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage requestStorage;
    private final ItemRequestMapper requestMapper;

    @Override
    public ItemRequestDto create(long requesterId, ItemRequestDto dto) {
        log.info("Создание запроса от пользователя requesterId={}", requesterId);
        ItemRequest toCreate = requestMapper.toItemRequest(dto, requesterId);
        ItemRequest saved = requestStorage.create(toCreate);
        log.debug("Создан запрос id={} от requesterId={}", saved.getId(), saved.getRequesterId());
        return requestMapper.toItemRequestDto(saved);
    }

    @Override
    public ItemRequestDto getById(long requestId) {
        log.info("Получение запроса по id={}", requestId);
        return requestMapper.toItemRequestDto(requestStorage.getById(requestId));
    }

    @Override
    public Collection<ItemRequestDto> getAll() {
        log.debug("Получение всех запросов");
        return requestStorage.getAll().stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getByRequester(long requesterId) {
        log.info("Получение запросов по requesterId={}", requesterId);
        return requestStorage.getByRequester(requesterId).stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long requestId) {
        log.info("Удаление запроса id={}", requestId);
        requestStorage.delete(requestId);
        log.debug("Удалён запрос id={}", requestId);
    }
}

