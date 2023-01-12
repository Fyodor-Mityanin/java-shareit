package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserValidation userValidation
    ) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
    }

    public List<UserDto> getAll() {
        return UserMapper.toDtos(userRepository.getAll());
    }

    public UserDto create(UserDto userDto) {
        userValidation.validateCreation(userDto);
        Long userId = userRepository.add(UserMapper.toObject(userDto));
        userDto.setId(userId);
        return userDto;
    }

    public UserDto update(Long id, UserDto userDto) {
        userDto.setId(id);
        User updatedUser = userValidation.validateUpdateAndGet(userDto);
        UserDto updatedUserDto = UserMapper.toDto(updatedUser);
        if (userDto.getName() != null && !Objects.equals(updatedUserDto.getName(), userDto.getName())) {
            updatedUserDto.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !Objects.equals(updatedUserDto.getEmail(), userDto.getEmail())) {
            updatedUserDto.setEmail(userDto.getEmail());
        }
        updatedUser = UserMapper.toObject(updatedUserDto);
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    public User getById(Long id) {
        return userRepository.getUserById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Юзер с id %d не найден", id))
                );
    }

    public void delete(long id) {
        userValidation.validateDelete(id);
        userRepository.deleteById(id);
    }
}
