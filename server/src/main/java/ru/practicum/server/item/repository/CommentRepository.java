package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItem_IdOrderByCreatedAsc(Long itemId);

    List<Comment> findByItem_IdInOrderByCreatedAsc(List<Long> itemIds);

    List<Comment> findAllByItem_Id(Long itemId);
}
