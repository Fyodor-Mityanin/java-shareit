package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Для поиска вещей должен быть организован поиск. Чтобы воспользоваться нужной вещью, её требуется забронировать.
 * Бронирование, или Booking — ещё одна важная сущность приложения. Бронируется вещь всегда на определённые даты.
 * Владелец вещи обязательно должен подтвердить бронирование.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Дата и время начала бронирования
     */
    @Column(name = "start_time")
    private LocalDateTime startDate;

    /**
     * Дата и время конца бронирования
     */
    @Column(name = "end_time")
    private LocalDateTime endDate;

    /**
     * Вещь, которую пользователь бронирует
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    /**
     * Пользователь, который осуществляет бронирование
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;

    /**
     * Статус бронирования
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;

        Booking booking = (Booking) o;

        return getId() != null ? getId().equals(booking.getId()) : booking.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
