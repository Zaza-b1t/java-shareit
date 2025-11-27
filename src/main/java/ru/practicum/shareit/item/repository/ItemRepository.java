package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByAvailableTrueAndNameContainingIgnoreCase(String text);

    List<Item> findAllByAvailableTrueAndDescriptionContainingIgnoreCase(String text);
}
