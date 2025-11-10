package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) long requesterId,
                                 @RequestBody ItemRequestDto dto) {
        return requestService.create(requesterId, dto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId) {
        return requestService.getById(requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAll() {
        return requestService.getAll();
    }

    @GetMapping("/own")
    public Collection<ItemRequestDto> getByRequester(@RequestHeader(USER_HEADER) long requesterId) {
        return requestService.getByRequester(requesterId);
    }

    @DeleteMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long requestId) {
        requestService.delete(requestId);
    }
}
