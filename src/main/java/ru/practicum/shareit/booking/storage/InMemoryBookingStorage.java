package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryBookingStorage implements BookingStorage {

    private final Map<Long, Booking> bookings = new HashMap<>();
    private long currentId = 0;
    private final ItemStorage itemStorage;

    @Override
    public Booking create(Booking booking) {
        booking.setId(genNextId());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking update(long id, Booking booking) {
        if (!bookings.containsKey(id)) {
            throw new EntityNotFoundException("Бронь с ID = " + id + " не найдена");
        }
        booking.setId(id);
        bookings.put(id, booking);
        return booking;
    }

    @Override
    public Booking getById(long id) {
        if (!bookings.containsKey(id)) {
            throw new EntityNotFoundException("Бронь с ID = " + id + " не найдена");
        }
        return bookings.get(id);
    }

    @Override
    public Collection<Booking> getAll() {
        return bookings.values();
    }

    @Override
    public Collection<Booking> getByBooker(long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookerId() == bookerId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Booking> getByOwner(long ownerId) {
        Collection<Item> ownerItems = itemStorage.getByOwner(ownerId);
        if (ownerItems.isEmpty()) return List.of();

        // 2) собираем их id
        Set<Long> itemIds = ownerItems.stream()
                .map(Item::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (itemIds.isEmpty()) return List.of();

        return bookings.values().stream()
                .filter(b -> itemIds.contains(b.getItemId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        if (!bookings.containsKey(id)) {
            throw new EntityNotFoundException("Бронь с ID = " + id + " не найдена");
        }
        bookings.remove(id);
    }

    private long genNextId() {
        return ++currentId;
    }
}
