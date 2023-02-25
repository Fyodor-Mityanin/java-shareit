package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exeptions.RequestIsEmptyException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestRequestDto request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new RequestIsEmptyException("Реквест пуст");
        }
        return itemRequestService.create(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        if (from != null && size != null) {
            Sort sort = Sort.by("created").descending();
            Pageable pageable = PageRequest.of(from, size, sort);
            return itemRequestService.findAllExceptRequester(userId, pageable);
        } else {
            return itemRequestService.findAllExceptRequester(userId);
        }
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getOneById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return itemRequestService.getOneById(userId, requestId);
    }
}
