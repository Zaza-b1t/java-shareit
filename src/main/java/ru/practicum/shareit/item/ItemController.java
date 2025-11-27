package ru.practicum.shareit.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) long ownerId,
                          @Valid @RequestBody ItemDto dto) {
        return itemService.create(ownerId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemService.getById(itemId, userId);
    }


    @GetMapping
    public Collection<ItemDto> getByOwner(@RequestHeader(USER_HEADER) long ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_HEADER) long ownerId,
                       @PathVariable long itemId) {
        itemService.delete(ownerId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader(USER_HEADER) long ownerId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto dto) {
        return itemService.patch(ownerId, itemId, dto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(USER_HEADER) long authorId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentCreateDto dto
    ) {
        return itemService.addComment(authorId, itemId, dto);
    }

}
