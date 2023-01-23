package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("/item create: " + itemDto.toString());
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId) {
        log.info("/item/" + itemId + " patch: " + itemDto.toString());
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getOneById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("/item/" + itemId + " getOneById");
        return itemService.getByIdWithBookings(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("/item findAllByUserId");
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    //тут почему-то NotBlank никак не реагирует на бланк
    public List<ItemDto> searchByName(@RequestParam @NotBlank String text) {
        log.info("/item/search" + text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchByName(text);
    }
}
