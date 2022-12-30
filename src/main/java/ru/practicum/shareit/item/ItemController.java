package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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

    @GetMapping("/{id}")
    public ItemDto getOneById(@PathVariable long id) {
        log.info("/item/" + id + " getOneById");
        return itemService.getById(id);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("/item findAllByUserId");
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestParam String text) {
        log.info("/item/search" + text);
        return itemService.searchByName(text);
    }
}
