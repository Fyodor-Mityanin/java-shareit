package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
    @Column(name = "is_available")
    private Boolean isAvailable;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id")
    @ToString.Exclude
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        return getId() != null ? getId().equals(item.getId()) : item.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
