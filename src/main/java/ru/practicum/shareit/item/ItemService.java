package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exeptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserValidation;

import java.util.Collections;
import java.util.List;

@Component
public class ItemService {

    private final ItemValidation itemValidation;
    private final UserValidation userValidation;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(
            ItemValidation itemValidation,
            UserValidation userValidation,
            ItemRepository itemRepository,
            ItemMapper itemMapper
    ) {
        this.itemValidation = itemValidation;
        this.userValidation = userValidation;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        itemDto.setOwner(userId);
        userValidation.validateItemCreate(userId);
        itemValidation.validateCreation(itemDto);
        itemDto.setOwner(userId);
        Long itemId = itemRepository.add(itemMapper.toObject(itemDto));
        itemDto.setId(itemId);
        return itemDto;
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        itemDto.setOwner(userId);
        itemValidation.validateUpdate(itemDto);
        ItemDto updatedItemDto;
        if (itemDto.getAvailable() != null && itemDto.getDescription() != null && itemDto.getName() != null) {
            Item item = itemMapper.toObject(itemDto);
            Item updatedItem = itemRepository.update(item);
            updatedItemDto = itemMapper.toDto(updatedItem);
        } else {
            if (itemDto.getAvailable() != null) {
                itemRepository.updateAvailable(itemDto.getId(), itemDto.getAvailable());
            } else if (itemDto.getDescription() != null) {
                itemRepository.updateDescription(itemDto.getId(), itemDto.getDescription());
            } else if (itemDto.getName() != null) {
                itemRepository.updateName(itemDto.getId(), itemDto.getName());
            }
            updatedItemDto = getById(itemDto.getId());
        }
        return updatedItemDto;
    }

    public ItemDto getById(Long id) {
        Item item = itemRepository.getItemById(id)
                .orElseThrow(
                        () -> new ItemNotFoundException(String.format("Предмет с id %d не найден", id))
                );
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAllByUserId(Long userId) {
        return itemMapper.toDtos(itemRepository.getAllByUserId(userId));
    }

    public List<ItemDto> searchByName(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemMapper.toDtos(itemRepository.searchByName(text));
    }
}
