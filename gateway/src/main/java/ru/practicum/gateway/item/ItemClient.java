package ru.practicum.gateway.item;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.ItemUpdateDto;

public class ItemClient extends BaseClient {
    public static final String API_PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(long ownerId, Long itemId, ItemUpdateDto itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getById(long requesterId, Long itemId) {
        return get("/" + itemId, requesterId);
    }

    public ResponseEntity<Object> getOwnerItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> addComment(long authorId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", authorId, commentDto);
    }
}
