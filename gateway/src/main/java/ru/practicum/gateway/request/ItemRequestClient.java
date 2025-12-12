package ru.practicum.gateway.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.request.dto.ItemRequestDto;

public class ItemRequestClient extends BaseClient {
    public static final String API_PREFIX = "/requests";

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getOwn(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllOthers(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
