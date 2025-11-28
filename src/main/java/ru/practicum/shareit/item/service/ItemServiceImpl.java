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
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Item item = itemMapper.toItem(dto, owner);
        Item saved = itemRepository.save(item);

        return itemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        log.info("Получение вещи по id={}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена."));

        ItemDto dto = itemMapper.toItemDto(item);

        List<Comment> comments = commentRepository.findAllByItem_IdOrderByCreatedAsc(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();
        dto.setComments(commentDtos);

        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            return dto;
        }

        List<Booking> bookings = bookingRepository
                .findAllByItem_IdInAndStatusOrderByStartDesc(List.of(itemId), BookingStatus.APPROVED);

        LocalDateTime now = LocalDateTime.now();

        bookings.stream()
                .filter(b -> !b.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getStart))
                .ifPresent(b -> dto.setLastBooking(
                        new BookingShortDto(b.getId(), b.getBooker().getId())
                ));

        bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(b -> dto.setNextBooking(
                        new BookingShortDto(b.getId(), b.getBooker().getId())
                ));

        return dto;
    }

    @Override
    public ItemDto update(long ownerId, long itemId, ItemDto dto) {
        log.info("Обновление вещи id={} пользователем ownerId={}", itemId, ownerId);
        Item existing = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Вещь с ID = " + itemId + " не найдена"));
        if (!existing.getOwner().getId().equals(ownerId)) {
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
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Удалять вещь может только владелец.");
        }
        itemRepository.deleteById(itemId);
        log.info("Вещь id={} удалена пользователем ownerId={}", itemId, ownerId);
    }

    @Override
    public Collection<ItemDto> getByOwner(long ownerId) {
        log.info("Получение списка вещей по ownerId={}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь с ID = " + ownerId + " не найден.");
        }

        List<Item> items = itemRepository.findAllByOwner_Id(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> bookings = bookingRepository
                .findAllByItem_IdInAndStatusOrderByStartDesc(itemIds, BookingStatus.APPROVED);

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<Comment> allComments = commentRepository
                .findAllByItem_IdInOrderByCreatedAsc(itemIds);

        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toItemDto(item);

                    List<Booking> itemBookings =
                            bookingsByItem.getOrDefault(item.getId(), List.of());

                    itemBookings.stream()
                            .filter(b -> !b.getStart().isAfter(now))
                            .max(Comparator.comparing(Booking::getStart))
                            .ifPresent(b -> dto.setLastBooking(
                                    new BookingShortDto(b.getId(), b.getBooker().getId())
                            ));

                    itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .ifPresent(b -> dto.setNextBooking(
                                    new BookingShortDto(b.getId(), b.getBooker().getId())
                            ));

                    List<CommentDto> commentDtos = commentsByItem
                            .getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(commentMapper::toDto)
                            .toList();

                    dto.setComments(commentDtos);

                    return dto;
                })
                .toList();
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
        if (!existing.getOwner().getId().equals(ownerId)) {
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
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        boolean hasCompletedBooking = bookingRepository
                .existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(
                        authorId,
                        itemId,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED
                );

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не может комментировать вещь, которую не арендовал");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }
}
