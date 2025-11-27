package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(long ownerId, ItemDto dto) {
        log.info("Создание вещи пользователем ownerId={}", ownerId);
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь с ID = " + ownerId + " не найден.");
        }
        Item saved = itemRepository.save(itemMapper.toItem(dto, ownerId));
        log.info("Создана вещь id={} для ownerId={}", saved.getId(), ownerId);
        return itemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        ItemDto dto = itemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(itemId);

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            User author = userRepository.findById(c.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Автор комментария не найден"));
            commentDtos.add(commentMapper.toDto(c, author));
        }
        dto.setComments(commentDtos);

        if (!Objects.equals(item.getOwnerId(), userId)) {
            return dto;
        }

        List<Booking> bookings = bookingRepository
                .findAllByItem_IdAndStatusOrderByStartDesc(itemId, BookingStatus.APPROVED);

        LocalDateTime now = LocalDateTime.now();

        Booking last = null;
        Booking next = null;

        for (Booking b : bookings) {
            if (!b.getStart().isAfter(now)) { // start <= now
                if (last == null || b.getStart().isAfter(last.getStart())) {
                    last = b;
                }
            } else {
                if (next == null || b.getStart().isBefore(next.getStart())) {
                    next = b;
                }
            }
        }

        if (last != null)
            dto.setLastBooking(new BookingShortDto(last.getId(), last.getBooker().getId()));

        if (next != null)
            dto.setNextBooking(new BookingShortDto(next.getId(), next.getBooker().getId()));

        return dto;
    }




    @Override
    public ItemDto update(long ownerId, long itemId, ItemDto dto) {
        log.info("Обновление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена"));
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Редактировать вещь может только владелец");
        }
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setAvailable(dto.getAvailable());
        Item updated = itemRepository.save(existing);
        log.info("Вещь id={} обновлена пользователем ownerId={}", itemId, ownerId);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public void delete(long ownerId, long itemId) {
        log.info("Удаление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена"));
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Удалять вещь может только владелец.");
        }
        itemRepository.deleteById(itemId);
        log.info("Вещь id={} удалена пользователем ownerId={}", itemId, ownerId);
    }

    @Override
    public Collection<ItemDto> getByOwner(long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDto> result = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            ItemDto dto = itemMapper.toItemDto(item);
            List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId());
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment c : comments) {
                User author = userRepository.findById(c.getAuthorId())
                        .orElseThrow(() -> new EntityNotFoundException("Автор комментария не найден"));
                commentDtos.add(commentMapper.toDto(c, author));
            }
            dto.setComments(commentDtos);

            List<Booking> bookings = bookingRepository
                    .findAllByItem_IdAndStatusOrderByStartDesc(item.getId(), BookingStatus.APPROVED);

            Booking last = null;
            Booking next = null;

            for (Booking b : bookings) {
                if (!b.getStart().isAfter(now)) {
                    if (last == null || b.getStart().isAfter(last.getStart())) {
                        last = b;
                    }
                } else {
                    if (next == null || b.getStart().isBefore(next.getStart())) {
                        next = b;
                    }
                }
            }

            if (last != null)
                dto.setLastBooking(new BookingShortDto(last.getId(), last.getBooker().getId()));

            if (next != null)
                dto.setNextBooking(new BookingShortDto(next.getId(), next.getBooker().getId()));

            result.add(dto);
        }

        return result;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.info("Поиск вещей по тексту='{}'", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> byName = itemRepository.findAllByAvailableTrueAndNameContainingIgnoreCase(text);
        List<Item> byDescription = itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCase(text);
        return Stream.concat(byName.stream(), byDescription.stream())
                .distinct()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto patch(long ownerId, long itemId, ItemDto dto) {
        log.info("Частичное обновление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена"));
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new EntityNotFoundException("Редактировать вещь может только владелец.");
        }

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new ValidationException("Название вещи не может быть пустым.");
            }
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            existing.setAvailable(dto.getAvailable());
        }

        Item updated = itemRepository.save(existing);
        log.info("Вещь id={} частично обновлена пользователем ownerId={}", itemId, ownerId);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public boolean existsById(long id) {
        return itemRepository.existsById(id);
    }

    @Override
    public CommentDto addComment(long authorId, long itemId, CommentCreateDto dto) {

        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        LocalDateTime now = LocalDateTime.now();
        boolean hasCompletedBooking = bookingRepository
                .findAllByBooker_IdOrderByStartDesc(authorId).stream()
                .anyMatch(b ->
                        b.getItem().getId().equals(itemId) &&
                                b.getEnd().isBefore(now) &&
                                b.getStatus() == BookingStatus.APPROVED
                );

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не может комментировать вещь, которую не арендовал");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        User author = userRepository.findById(authorId).get();
        return commentMapper.toDto(saved, author);
    }


}
