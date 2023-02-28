package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * Класс пользователя
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя или логин пользователя
     */
    @Column(nullable = false)
    private String name;

    /**
     * Адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
     */
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return email != null && email.equals(((User) o).getEmail());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
