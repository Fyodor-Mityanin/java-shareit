package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Класс пользователя
 */
@Data
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    private final Long id;

    /**
     * Имя или логин пользователя
     */
    @NotBlank
    private final String name;

    /**
     * Адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
     */
    @NonNull
    @NotBlank
    @Email(message = "Почта должна быть валидна")
    private final String email;
}
