package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "id", nullable = false)
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
    @ToString.Exclude
    private User requester;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Item> items = new ArrayList<>();

    /**
     * Дата и время создания запроса
     */
    @Column
    private LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;

        ItemRequest that = (ItemRequest) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
