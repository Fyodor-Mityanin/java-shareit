package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exeptions.ItemNotFoundException;
import ru.practicum.shareit.error.exeptions.ItemOwnershipException;
import ru.practicum.shareit.error.exeptions.ItemValidationException;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

@Component
public class ItemValidation {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    public ItemValidation(
            ItemRepository itemRepository,
            UserRepository userRepository
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    public void validateCreation(ItemDto itemDto) {
        userRepository.findById(itemDto.getOwner()).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", itemDto.getOwner()))
        );
        if (itemDto.getAvailable() == null) {
            throw new ItemValidationException("Необходима доступность");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemValidationException("Необходимо имя");
        }
        if (itemDto.getDescription() == null) {
            throw new ItemValidationException("Необходимо описание");
        }
    }

    public Item validateUpdateAndGet(ItemDto itemDto) {
        if (itemDto.getOwner() == null) {
            throw new ItemValidationException("Юзер не авторизирован");
        }
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Товара не существует")
                );
        if (!itemDto.getOwner().equals(item.getOwner().getId())) {
            throw new ItemOwnershipException("Вы не хозяин предмета");
        }
        return item;
    }
}
