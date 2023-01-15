package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exeptions.ItemNotFoundException;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Component
public class ItemService {

    private final ItemValidation itemValidation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(
            ItemValidation itemValidation,
            ItemRepository itemRepository,
            UserRepository userRepository
    ) {
        this.itemValidation = itemValidation;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        itemDto.setOwner(userId);
        User user = userRepository.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        itemValidation.validateCreation(itemDto);
        itemDto.setOwner(userId);
        Long itemId = itemRepository.add(ItemMapper.toObject(itemDto, user));
        itemDto.setId(itemId);
        return itemDto;
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        itemDto.setOwner(userId);
        User user = userRepository.getUserById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        Item updatedItem = itemValidation.validateUpdateAndGet(itemDto);
        ItemDto updatedItemDto = ItemMapper.toDto(updatedItem);
        if (itemDto.getAvailable() != null && updatedItemDto.getAvailable() != itemDto.getAvailable()) {
            updatedItemDto.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null && !Objects.equals(updatedItemDto.getDescription(), itemDto.getDescription())) {
            updatedItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null && !Objects.equals(updatedItemDto.getName(), itemDto.getName())) {
            updatedItemDto.setName(itemDto.getName());
        }
        updatedItem = ItemMapper.toObject(updatedItemDto, user);
        updatedItem = itemRepository.update(updatedItem);
        return ItemMapper.toDto(updatedItem);
    }

    public ItemDto getById(Long id) {
        Item item = itemRepository.getItemById(id)
                .orElseThrow(
                        () -> new ItemNotFoundException(String.format("Предмет с id %d не найден", id))
                );
        return ItemMapper.toDto(item);
    }

    public List<ItemDto> getAllByUserId(Long userId) {
        return ItemMapper.toDtos(itemRepository.getAllByUserId(userId));
    }

    public List<ItemDto> searchByName(String text) {
        return ItemMapper.toDtos(itemRepository.searchByName(text));
    }
}
