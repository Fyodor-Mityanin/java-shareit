package ru.practicum.shareit.item.dto;

import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .owner(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(new ArrayList<>())
                .build();
    }

    public static List<ItemDto> toDtos(@NonNull List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        items.forEach(item -> dtos.add(toDto(item)));
        return dtos;
    }

    public static Item toObject(@NonNull ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(user);
        if (itemRequest != null) {
            item.setRequest(itemRequest);
        }
        item.setIsAvailable(itemDto.getAvailable());
        return item;
    }
}
