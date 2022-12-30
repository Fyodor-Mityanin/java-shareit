package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest.
 * Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске. В запросе указывается, что именно он ищет.
 * В ответ на запрос другие пользователи могут добавить нужную вещь.
 */
@Data
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса
     */
    private long id;
    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    private String description;
    /**
     * Пользователь, создавший запрос
     */
    private String requester;
    /**
     * Дата и время создания запроса
     */
    private LocalDateTime created;
}
