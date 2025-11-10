package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage{

    private final Map<Long,Item> items = new HashMap<>();
    private long currentId = 0;

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long id, Item item) {
        if(!exists(id)) {
            throw new EntityNotFoundException("Вещь с ID = " + id + " не найден.");
        }
        item.setId(id);
        items.put(id,item);
        return item;
    }

    @Override
    public Item getById(long id) {
        if(!exists(id)) {
            throw new EntityNotFoundException("Вещь с ID = " + id + " не найден.");
        };
        return items.get(id);
    }

    @Override
    public Collection<Item> getByOwner(long id) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == id)
                .toList();
    }

    @Override
    public void delete(long itemId) {
        if(!exists(itemId)) {
            throw new EntityNotFoundException("Вещь с ID = " + itemId + " не найден.");
        }
        items.remove(itemId);
    }

    @Override
    public boolean exists(long id) {
        return items.containsKey(id);
    }

    @Override
    public Collection<Item> search(String text) {
        if(text == null || text.isBlank()) {
            return List.of();
        }

        String query = text.toLowerCase();
        List<Item> result = new ArrayList<>();

        for(Item item : items.values()) {
            if(Boolean.TRUE.equals(item.isAvailable())) {
                String name = item.getName() == null ? "" : item.getName().toLowerCase();
                String desc = item.getDescription() == null ? "" : item.getDescription().toLowerCase();

                if(name.contains(query) || desc.contains(query)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private long getNextId() {
        return ++currentId;
    }
}
