package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRequestStorage implements ItemRequestStorage {

    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private long currentId = 0;

    @Override
    public ItemRequest create(ItemRequest request) {
        request.setId(genNextId());
        if (request.getCreated() == null) {
            request.setCreated(LocalDateTime.now());
        }
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public ItemRequest getById(long id) {
        ItemRequest req = requests.get(id);
        if (req == null) {
            throw new EntityNotFoundException("Запрос с ID = " + id + " не найден");
        }
        return req;
    }

    @Override
    public Collection<ItemRequest> getAll() {
        return requests.values();
    }

    @Override
    public Collection<ItemRequest> getByRequester(long requesterId) {
        return requests.values().stream()
                .filter(r -> Objects.equals(r.getRequesterId(), requesterId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        if (!requests.containsKey(id)) {
            throw new EntityNotFoundException("Запрос с ID = " + id + " не найден");
        }
        requests.remove(id);
    }

    private long genNextId() {
        return ++currentId;
    }
}
