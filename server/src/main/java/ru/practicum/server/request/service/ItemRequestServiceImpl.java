package ru.practicum.server.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.server.exception.EntityNotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestItemDto;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository,
                                  UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requester = getUserOrThrow(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return toDto(saved, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        getUserOrThrow(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);
        Map<Long, List<ItemRequestItemDto>> itemsByRequest = getItemsByRequestIds(requests);
        return requests.stream()
                .map(request -> toDto(request, itemsByRequest.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllOthers(Long userId) {
        getUserOrThrow(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNotOrderByCreatedDesc(userId);
        Map<Long, List<ItemRequestItemDto>> itemsByRequest = getItemsByRequestIds(requests);
        return requests.stream()
                .map(request -> toDto(request, itemsByRequest.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserOrThrow(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
        List<ItemRequestItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
        return toDto(request, items);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    private Map<Long, List<ItemRequestItemDto>> getItemsByRequestIds(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        if (requestIds.isEmpty()) {
            return Map.of();
        }
        return itemRepository.findByRequestIdInOrderById(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId,
                        Collectors.mapping(this::toItemDto, Collectors.toList())));
    }

    private ItemRequestDto toDto(ItemRequest itemRequest, List<ItemRequestItemDto> items) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }

    private ItemRequestItemDto toItemDto(Item item) {
        return new ItemRequestItemDto(item.getId(), item.getName(), item.getOwnerId());
    }
}
