package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(long bookerId, BookingDto dto) {
        log.info("Создание бронирования пользователем {}", bookerId);
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("Укажите даты начала и окончания брони.");
        }
        if (!start.isBefore(end)) {
            throw new ValidationException("Дата начала должна быть строго раньше даты окончания.");
        }

        Item item = itemStorage.getById(dto.getItemId());
        if (Boolean.FALSE.equals(item.isAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }
        if (item.getOwnerId() != null && item.getOwnerId() == bookerId) {
            throw new ValidationException("Владелец не может бронировать свою вещь.");
        }

        Booking toCreate = bookingMapper.toBooking(dto, bookerId);
        toCreate.setStatus(BookingStatus.WAITING);

        Booking saved = bookingStorage.create(toCreate);
        log.info("Создана бронь id={} для itemId={} bookerId={}", saved.getId(), saved.getItemId(), saved.getBookerId());

        return bookingMapper.toBookingDto(saved);
    }

    @Override
    public BookingDto updateStatus(long ownerId, long bookingId, boolean approved) {
        log.info("Изменение статуса брони {} владельцем {}", bookingId, ownerId);
        Booking booking = bookingStorage.getById(bookingId);
        Item item = itemStorage.getById(booking.getItemId());

        if (item.getOwnerId() == null || item.getOwnerId() != ownerId) {
            throw new ValidationException("Подтверждать/отклонять бронь может только владелец вещи.");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус брони уже установлен и не может быть изменён.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingStorage.update(bookingId, booking);
        log.info("Изменён статус брони id={} → {}", bookingId, updated.getStatus());

        return bookingMapper.toBookingDto(updated);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        log.info("Запрос бронирования {} пользователем {}", bookingId, userId);
        Booking booking = bookingStorage.getById(bookingId);
        Item item = itemStorage.getById(booking.getItemId());

        boolean isBooker = booking.getBookerId() == userId;
        boolean isOwner = item.getOwnerId() != null && item.getOwnerId() == userId;

        if (!isBooker && !isOwner) {
            throw new ValidationException("Недостаточно прав для просмотра этой брони.");
        }
        log.info("Бронь найдена: {}", bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getByBooker(long bookerId) {
        log.info("Получение списка бронирований по bookerId={}", bookerId);
        return bookingStorage.getByBooker(bookerId).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getByOwner(long ownerId) {
        log.info("Получение списка бронирований по ownerId={}", ownerId);
        return bookingStorage.getByOwner(ownerId).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
