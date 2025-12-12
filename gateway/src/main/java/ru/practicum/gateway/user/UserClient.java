package ru.practicum.gateway.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

public class UserClient extends BaseClient {
    public static final String API_PREFIX = "/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> update(Long userId, UserUpdateDto userDto) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }
}
