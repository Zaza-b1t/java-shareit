package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(
            Long bookerId,
            LocalDateTime end
    );

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start
    );

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(
            Long bookerId,
            BookingStatus status
    );

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_IdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findAllByItem_IdAndStatusOrderByStartDesc(Long itemId, BookingStatus status);
}
