package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getByIdWithBookings(Long userId, Long itemId);

    List<ItemDto> getAllByUserId(Long userId);

    List<ItemDto> searchByName(String text);

    CommentDto createComment(Long userId, Long itemId, String text);
}
