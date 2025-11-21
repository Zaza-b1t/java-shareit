package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.Booking;

import java.util.Collection;

public interface BookingStorage {
    Booking create(Booking booking);

    Booking update(long id, Booking booking);

    Booking getById(long id);

    Collection<Booking> getAll();

    Collection<Booking> getByBooker(long bookerId);

    Collection<Booking> getByOwner(long ownerId);

    void delete(long id);

    boolean exists(long id);
}
