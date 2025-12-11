package ru.practicum.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
        booker = userRepository.save(new User(null, "booker", "booker@mail.ru"));

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        itemRepository.save(item);
    }

    @Test
    void testCreateBooking() {
        BookingDto dto = new BookingDto(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId(),
                null,
                null,
                null,
                null
        );

        BookingDto returned = bookingService.create(booker.getId(), dto);

        assertThat(returned.getId()).isNotNull();
        assertThat(returned.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(returned.getItemId()).isEqualTo(item.getId());
        assertThat(returned.getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void testApproveBooking() {
        BookingDto created = bookingService.create(
                booker.getId(),
                new BookingDto(
                        null,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        item.getId(),
                        null,
                        null,
                        null,
                        null
                )
        );

        BookingDto updated = bookingService.updateStatus(owner.getId(), created.getId(), true);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testGetBookingById() {
        BookingDto created = bookingService.create(
                booker.getId(),
                new BookingDto(
                        null,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        item.getId(),
                        null,
                        null,
                        null,
                        null
                )
        );

        BookingDto found = bookingService.getById(booker.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getBookerId()).isEqualTo(booker.getId());
    }
}
