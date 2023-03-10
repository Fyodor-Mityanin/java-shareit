package ru.practicum.shareit.item.dto;

import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toDto(@NonNull Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getIsAvailable());
        itemDto.setOwner(item.getOwner().getId());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        itemDto.setComments(new ArrayList<>());
        return itemDto;
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
