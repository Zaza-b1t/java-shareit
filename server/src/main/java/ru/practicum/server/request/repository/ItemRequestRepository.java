package ru.practicum.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_IdOrderByCreatedDesc(Long requesterId);

    List<ItemRequest> findAllByRequester_IdNotOrderByCreatedDesc(Long requesterId);
}
