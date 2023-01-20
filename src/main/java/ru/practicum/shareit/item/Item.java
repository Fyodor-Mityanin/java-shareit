package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * Основная сущность сервиса, вокруг которой будет строиться вся дальнейшая работа, — вещь.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {

    /**
     * Уникальный идентификатор вещи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое название
     */
    @Column(nullable = false)
    private String name;

    /**
     * Развёрнутое описание;
     */
    @Column
    private String description;

    /**
     * Статус о том, доступна или нет вещь для аренды;
     */
    @Column
    private Boolean available;


    @ManyToOne
    @JoinColumn(nullable = false, name = "owner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
