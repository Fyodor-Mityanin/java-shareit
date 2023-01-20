package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.Instant;

/**
 * Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest.
 * Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске. В запросе указывается, что именно он ищет.
 * В ответ на запрос другие пользователи могут добавить нужную вещь.
 */
@Entity
@Table(name = "request")
@Getter
@Setter
@ToString
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    @Column
    private String description;

    /**
     * Пользователь, создавший запрос
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "requester")
    private User requester;

    /**
     * Дата и время создания запроса
     */
    @Column
    private Instant created = Instant.now();
}
