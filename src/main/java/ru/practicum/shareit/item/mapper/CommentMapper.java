package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment, User author) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                author.getName(),
                comment.getCreated()
        );
    }
}
