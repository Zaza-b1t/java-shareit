package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(long bookerId, BookingDto dto) {
        log.info("Создание бронирования пользователем {}", bookerId);

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена."));

        if (!Boolean.TRUE.equals(item.isAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        if (item.getOwnerId().equals(bookerId)) {
            throw new EntityNotFoundException("Владелец не может бронировать свою вещь.");
        }

        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Некорректный период бронирования.");
        }

        Booking booking = bookingMapper.toBooking(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }


    @Override
    public BookingDto updateStatus(long ownerId, long bookingId, boolean approved) {
        log.info("Изменение статуса брони {} владельцем {}", bookingId, ownerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Бронь с ID = " +
                bookingId + " не найден."));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " +
                booking.getItem().getId() + " не найден."));

        if (item.getOwnerId() == null || !item.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Подтверждать/отклонять бронь может только владелец вещи.");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус брони уже установлен и не может быть изменён.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);
        log.info("Изменён статус брони id={} → {}", bookingId, updated.getStatus());

        return bookingMapper.toDto(updated);
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        log.info("Запрос бронирования {} пользователем {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Бронь с ID = " +
                bookingId + " не найден."));

        Item item = booking.getItem();

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = item.getOwnerId() != null && item.getOwnerId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ValidationException("Недостаточно прав для просмотра этой брони.");
        }
        log.info("Бронь найдена: {}", bookingId);
        return bookingMapper.toDto(booking);
    }

    @Override
    public Collection<BookingDto> getByBooker(long bookerId, String stateText) {

        if (!userRepository.existsById(bookerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        String normalized = stateText == null ? "ALL" : stateText.toUpperCase();

        BookingState state;
        try {
            state = BookingState.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + stateText);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId);

        return list.stream()
                .filter(b -> switch (state) {
                    case ALL -> true;
                    case CURRENT -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
                    case PAST -> b.getEnd().isBefore(now);
                    case FUTURE -> b.getStart().isAfter(now);
                    case WAITING -> b.getStatus() == BookingStatus.WAITING;
                    case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
                })
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getByOwner(long ownerId, String stateText) {
        log.info("Получение списка бронирований по ownerId={} с state={}", ownerId, stateText);

        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь с ID = " + ownerId + " не найден.");
        }

        String normalized = (stateText == null) ? "ALL" : stateText.toUpperCase();

        BookingState state;
        try {
            state = BookingState.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + stateText);
        }

        List<Booking> all = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return all.stream()
                .filter(b -> switch (state) {
                    case ALL -> true;
                    case CURRENT -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
                    case PAST -> b.getEnd().isBefore(now);
                    case FUTURE -> b.getStart().isAfter(now);
                    case WAITING -> b.getStatus() == BookingStatus.WAITING;
                    case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
                })
                .map(bookingMapper::toDto)
                .toList();
    }


}
