package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A DTO for the {@link ru.practicum.shareit.request.model.ItemRequest} entity
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @Size(max = 255)
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}