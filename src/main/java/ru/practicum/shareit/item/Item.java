package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Основная сущность сервиса, вокруг которой будет строиться вся дальнейшая работа, — вещь.
 */
@Data
@Builder
public class Item {
    /**
     * Уникальный идентификатор вещи
     */
    private final Long id;
    /**
     * Краткое название
     */
    private final String name;
    /**
     * Развёрнутое описание;
     */
    private final String description;
    /**
     * Статус о том, доступна или нет вещь для аренды;
     */
    private final Boolean available;
    /**
     * Владелец вещи;
     */
    private final User owner;
    /**
     * Если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос.
     */
    private final ItemRequest request;
}
