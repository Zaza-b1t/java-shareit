package ru.practicum.server.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "ownerId", ignore = true)
    Item toItem(ItemDto itemDto);
}