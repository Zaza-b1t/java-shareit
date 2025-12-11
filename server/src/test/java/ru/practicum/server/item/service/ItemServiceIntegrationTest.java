package ru.practicum.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
        booker = userRepository.save(new User(null, "booker", "booker@mail.ru"));

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Хорошая дрель");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        itemRepository.save(item);
    }

    @Test
    void testCreateItem() {
        ItemDto dto = new ItemDto(null, "Молоток", "Стальной", true, null);

        ItemDto saved = itemService.create(owner.getId(), dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Молоток");
    }

    @Test
    void testUpdateItem() {
        ItemDto update = new ItemDto(null, "Обновлённая дрель", "обновление", true, null);

        ItemDto updated = itemService.update(owner.getId(), item.getId(), update);

        assertThat(updated.getName()).isEqualTo("Обновлённая дрель");
    }

    @Test
    void testGetOwnerItems() {
        List<?> items = itemService.getOwnerItems(owner.getId());

        assertThat(items).hasSize(1);
    }

    @Test
    void testSearch() {
        List<?> found = itemService.search("дрель");

        assertThat(found).hasSize(1);
    }

    @Test
    void testAddComment() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto comment = new CommentDto(null, "Отличная вещь!", null, null);

        CommentDto saved = itemService.addComment(booker.getId(), item.getId(), comment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo("Отличная вещь!");
        assertThat(saved.getAuthorName()).isEqualTo(booker.getName());
    }
}
