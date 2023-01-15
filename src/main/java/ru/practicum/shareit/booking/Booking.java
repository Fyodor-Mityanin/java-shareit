package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Для поиска вещей должен быть организован поиск. Чтобы воспользоваться нужной вещью, её требуется забронировать.
 * Бронирование, или Booking — ещё одна важная сущность приложения. Бронируется вещь всегда на определённые даты.
 * Владелец вещи обязательно должен подтвердить бронирование.
 */
@Data
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    private long id;
    /**
     * Дата и время начала бронирования
     */
    private LocalDateTime start;
    /**
     * Дата и время конца бронирования
     */
    private LocalDateTime end;
    /**
     * Вещь, которую пользователь бронирует
     */
    private Item item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private User booker;
    /**
     * Статус бронирования
     */
    private BookingStatus status;
}
