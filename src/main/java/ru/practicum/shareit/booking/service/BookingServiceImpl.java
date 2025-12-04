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

        if (item.getOwner().getId().equals(bookerId)) {
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

        if (item.getOwner().getId() == null || !item.getOwner().getId().equals(ownerId)) {
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
        boolean isOwner = item.getOwner().getId() != null && item.getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ValidationException("Недостаточно прав для просмотра этой брони.");
        }
        log.info("Бронь найдена: {}", bookingId);
        return bookingMapper.toDto(booking);
    }

    @Override
    public Collection<BookingDto> getByBooker(long bookerId, BookingState state) {

        if (!userRepository.existsById(bookerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings =
                    bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId);
            case CURRENT -> bookings =
                    bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                            bookerId, now, now
                    );
            case PAST -> bookings =
                    bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                            bookerId, now
                    );
            case FUTURE -> bookings =
                    bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                            bookerId, now
                    );
            case WAITING -> bookings =
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                            bookerId, BookingStatus.WAITING
                    );
            case REJECTED -> bookings =
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                            bookerId, BookingStatus.REJECTED
                    );
            default -> throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getByOwner(long ownerId, BookingState state) {

        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings =
                    bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
            case CURRENT -> bookings =
                    bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                            ownerId, now, now
                    );
            case PAST -> bookings =
                    bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
                            ownerId, now
                    );
            case FUTURE -> bookings =
                    bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
                            ownerId, now
                    );
            case WAITING -> bookings =
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                            ownerId, BookingStatus.WAITING
                    );
            case REJECTED -> bookings =
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                            ownerId, BookingStatus.REJECTED
                    );
            default -> throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}
