package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest.
 * Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске. В запросе указывается, что именно он ищет.
 * В ответ на запрос другие пользователи могут добавить нужную вещь.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    @Column
    @Size(max = 255)
    private String description;

    /**
     * Пользователь, создавший запрос
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "requester_id")
    private User requester;

    /**
     * Дата и время создания запроса
     */
    @Column
    private Instant created = Instant.now();
}
