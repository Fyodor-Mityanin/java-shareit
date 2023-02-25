package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(UserMapper.toDto(itemRequest.getRequester()))
                .items(Collections.emptyList())
                .build();
    }

    public static ItemRequest toObject(@NonNull ItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setRequester(requester);
        return itemRequest;
    }

    public static ItemRequest toObject(@NonNull ItemRequestRequestDto itemRequestRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestRequestDto.getDescription());
        itemRequest.setRequester(requester);
        return itemRequest;
    }

    public static List<ItemRequestDto> toDtos(@NonNull List<ItemRequest> requests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        requests.forEach(request -> dtos.add(toDto(request)));
        return dtos;
    }
}
