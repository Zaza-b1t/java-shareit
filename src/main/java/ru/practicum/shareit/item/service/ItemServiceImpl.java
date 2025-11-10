package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long ownerId, ItemDto dto) {
        log.info("Создание вещи пользователем ownerId={}", ownerId);
        if (!userStorage.exists(ownerId)) {
            throw new EntityNotFoundException("Пользователь с ID = " + ownerId + " не найден.");
        }
        Item saved = itemStorage.create(itemMapper.toItem(dto, ownerId));
        log.debug("Создана вещь id={} для ownerId={}", saved.getId(), ownerId);
        return itemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(long ownerId, long itemId, ItemDto dto) {
        log.info("Обновление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemStorage.getById(itemId);
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена для пользователя " + ownerId);
        }
        Item toUpdate = itemMapper.toItem(dto, ownerId);
        toUpdate.setId(itemId);
        Item updated = itemStorage.update(itemId, toUpdate);
        log.debug("Вещь id={} обновлена пользователем ownerId={}", itemId, ownerId);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getById(long id) {
        log.info("Получение вещи по id={}", id);
        return itemMapper.toItemDto(itemStorage.getById(id));
    }

    @Override
    public Collection<ItemDto> getByOwner(long ownerId) {
        log.debug("Получение списка вещей по ownerId={}", ownerId);
        return itemStorage.getByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long ownerId, long itemId) {
        log.info("Удаление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemStorage.getById(itemId);
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Удалять вещь может только владелец.");
        }
        itemStorage.delete(itemId);
        log.debug("Вещь id={} удалена пользователем ownerId={}", itemId, ownerId);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.debug("Поиск вещей по тексту='{}'", text);
        return itemStorage.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto patch(long ownerId, long itemId, ItemDto dto) {
        log.info("Частичное обновление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemStorage.getById(itemId);
        if (existing.getOwnerId() == null || existing.getOwnerId() != ownerId) {
            throw new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена для пользователя " + ownerId);
        }

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new ValidationException("Название вещи не может быть пустым.");
            }
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            existing.setAvailable(dto.getAvailable());
        }

        Item updated = itemStorage.update(itemId, existing);
        log.debug("Вещь id={} частично обновлена пользователем ownerId={}", itemId, ownerId);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public boolean existsById(long id) {
        return itemStorage.exists(id);
    }
}
