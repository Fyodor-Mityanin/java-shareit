package ru.practicum.shareit.item.dto;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    private final UserRepository userRepository;

    @Autowired
    public ItemMapper(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public ItemDto toDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public List<ItemDto> toDtos(@NonNull List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        items.forEach(item -> dtos.add(toDto(item)));
        return dtos;
    }

    public Item toObject(@NonNull ItemDto itemDto) {
        User user = userRepository.getUserById(itemDto.getOwner()).orElseThrow();
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .build();
    }



}
