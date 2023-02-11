package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

interface UserService {

    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    User getById(Long id);

    void delete(long id);
}
