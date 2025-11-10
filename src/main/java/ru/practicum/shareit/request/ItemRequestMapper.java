package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto dto, long requesterId) {
        if (dto == null) return null;
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setRequesterId(requesterId);
        request.setCreated(dto.getCreated() != null ? dto.getCreated() : LocalDateTime.now());
        return request;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest request) {
        if (request == null) return null;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setRequesterId(request.getRequesterId());
        dto.setCreated(request.getCreated());
        return dto;
    }
}
