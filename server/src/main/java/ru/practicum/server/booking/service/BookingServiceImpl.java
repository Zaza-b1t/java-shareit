package ru.practicum.server.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.model.BookingState;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.exception.EntityNotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(Long bookerId, BookingDto bookingDto) {
        validateDates(bookingDto);
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
        if (bookerId.equals(item.getOwnerId())) {
            throw new EntityNotFoundException("Владелец не может бронировать свою вещь");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setId(null);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));
        if (!ownerId.equals(booking.getItem().getOwnerId())) {
            throw new ValidationException("Только владелец может изменять статус");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new EntityNotFoundException("Статус уже установлен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Нет доступа к бронированию");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookerBookings(Long bookerId, String state) {
        ensureUserExists(bookerId);
        BookingState bookingState = BookingState.from(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, sort);
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, now, sort);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, now, sort);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, sort);
            default -> bookingRepository.findByBooker_Id(bookerId, sort);
        };
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        ensureUserExists(ownerId);
        BookingState bookingState = BookingState.from(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByOwnerId(ownerId, now, now, sort);
            case PAST -> bookingRepository.findPastByOwnerId(ownerId, now, sort);
            case FUTURE -> bookingRepository.findFutureByOwnerId(ownerId, now, sort);
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);
            default -> bookingRepository.findByOwnerId(ownerId, sort);
        };
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateDates(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Даты бронирования обязательны");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }
    }

    private void ensureUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}
