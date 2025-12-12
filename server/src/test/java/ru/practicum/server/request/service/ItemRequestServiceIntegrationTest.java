package ru.practicum.server.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User owner;

    @BeforeEach
    void setup() {
        requester = userRepository.save(new User(null, "requester", "req@mail.ru"));
        owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
    }

    @Test
    void testCreateRequest() {
        ItemRequestDto dto = new ItemRequestDto(null, "Нужен шуруповерт", null, List.of());

        ItemRequestDto created = requestService.create(requester.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужен шуруповерт");
        assertThat(created.getCreated()).isNotNull();
        assertThat(created.getItems()).isEmpty();
    }

    @Test
    void testGetOwnRequests() {
        requestService.create(requester.getId(), new ItemRequestDto(null, "Первый", null, List.of()));
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {
        }

        requestService.create(requester.getId(), new ItemRequestDto(null, "Второй", null, List.of()));

        List<ItemRequestDto> list = requestService.getOwn(requester.getId());

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getDescription()).isEqualTo("Второй");
        assertThat(list.get(1).getDescription()).isEqualTo("Первый");
    }

    @Test
    void testGetByIdWithItems() {
        ItemRequestDto reqDto = requestService.create(
                requester.getId(),
                new ItemRequestDto(null, "Нужна дрель", null, List.of())
        );

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        item.setRequestId(reqDto.getId());
        itemRepository.save(item);

        ItemRequestDto result = requestService.getById(requester.getId(), reqDto.getId());

        assertThat(result.getId()).isEqualTo(reqDto.getId());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Дрель");
    }
}
