package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("Create item: userId={}, itemDto={}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentRequestDto comment
    ) {
        log.info("Post comment: userId={}, itemId={}, comment={}", userId, itemId, comment);
        return itemClient.createComment(userId, itemId, comment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto,
            @PathVariable("id") long itemId
    ) {
        log.info("Update item: userId={}, itemDto={}, itemId={}", userId, itemDto, itemId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getOneById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.info("Get item: userId={}, itemId={}", userId, itemId);
        return itemClient.getByIdWithBookings(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all items: userId={}", userId);
        return itemClient.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestParam String text) {
        log.info("Search items: text={}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok().body("[]");
        }
        return itemClient.searchByName(text);
    }
}
