package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exeptions.UserDuplicateEmailException;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.error.exeptions.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Component
public class UserValidation {
    private static final String REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private final UserRepository userRepository;

    public UserValidation(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public void validateCreation(UserDto userDto) {
        if (userRepository.containsEmail(userDto.getEmail())) {
            throw new UserDuplicateEmailException(String.format("Юзер с email %s уже существует", userDto.getEmail()));
        }
        if (userDto.getEmail() == null) {
            throw new UserValidationException("Необходима почта");
        }
        if (!userDto.getEmail().matches(REGEX_PATTERN)) {
            throw new UserValidationException("Почта не валидна");
        }
    }

    public User validateUpdateAndGet(UserDto userDto) {
        if (userDto.getEmail() == null && userDto.getName() == null) {
            throw new UserValidationException("Юзер с пустыми полями");
        }
        if (userDto.getEmail() != null && userRepository.containsEmail(userDto.getEmail())) {
            throw new UserDuplicateEmailException(String.format("Юзер с email %s уже существует", userDto.getEmail()));
        }
        return userRepository.getUserById(userDto.getId()).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userDto.getId()))
        );
    }

    public void validateDelete(long id) {
        if (!userRepository.containsId(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
    }

    public void validateItemCreate(long id) {
        if (!userRepository.containsId(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
    }
}