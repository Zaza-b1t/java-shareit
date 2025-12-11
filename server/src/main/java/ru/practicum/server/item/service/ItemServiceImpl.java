package ru.practicum.server.item.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;

import ru.practicum.server.exception.EntityNotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.dto.BookingDatesDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemBookingsDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.mapper.ItemMapper;

import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;

import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;

import ru.practicum.server.request.repository.ItemRequestRepository;

import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        ensureUserExists(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        if (item.getRequestId() != null) {
            itemRequestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
        }
        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        ensureUserExists(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
        if (!ownerId.equals(item.getOwnerId())) {
            throw new EntityNotFoundException("Вещь не принадлежит пользователю");
        }
        String name = Objects.nonNull(itemDto.getName()) ? itemDto.getName() : item.getName();
        String description = Objects.nonNull(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription();
        Boolean available = Objects.nonNull(itemDto.getAvailable()) ? itemDto.getAvailable() : item.getAvailable();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);

        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemBookingsDto getById(Long requesterId, Long itemId) {
        ensureUserExists(requesterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
        List<CommentDto> comments = getCommentsForItem(item.getId());
        if (item.getOwnerId().equals(requesterId)) {
            return toItemBookings(item, comments);
        }
        return new ItemBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId(), null, null, comments);
    }

    @Override
    public List<ItemBookingsDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId);
        Map<Long, List<CommentDto>> comments = getCommentsForItems(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> toItemBookings(item, comments.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemBookingsDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        List<Item> items = itemRepository.search(text);
        Map<Long, List<CommentDto>> comments = getCommentsForItems(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> toItemBookings(item, comments.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public void deleteAllByOwnerId(Long ownerId) {
        itemRepository.deleteAllByOwnerId(ownerId);
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
        LocalDateTime now = LocalDateTime.now();
        boolean hadBooking = bookingRepository.existsByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(authorId, itemId,
                now, BookingStatus.APPROVED);
        if (!hadBooking) {
            throw new ValidationException(
                    "Только арендаторы могут оставлять комментарии");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);
        return toCommentDto(saved);
    }

    private void ensureUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    private ItemBookingsDto toItemBookings(Item item, List<CommentDto> comments) {
        List<Booking> bookings = bookingRepository.findByItem_Id(item.getId(), Sort.by("start"));
        LocalDateTime now = LocalDateTime.now();

        BookingDatesDto lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getStart))
                .map(this::toBookingDatesDto)
                .orElse(null);

        BookingDatesDto nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(this::toBookingDatesDto)
                .orElse(null);

        return new ItemBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId(), lastBooking, nextBooking, comments);
    }

    private BookingDatesDto toBookingDatesDto(Booking booking) {
        return new BookingDatesDto(booking.getId(), booking.getStart(), booking.getEnd());
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItem_IdOrderByCreatedAsc(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private Map<Long, List<CommentDto>> getCommentsForItems(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<CommentDto>> commentsByItem = new HashMap<>();
        commentRepository.findByItem_IdInOrderByCreatedAsc(itemIds)
                .forEach(comment -> commentsByItem
                        .computeIfAbsent(comment.getItem().getId(), key -> new java.util.ArrayList<>())
                        .add(toCommentDto(comment)));
        return commentsByItem;
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
