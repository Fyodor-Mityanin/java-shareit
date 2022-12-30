package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            UserValidation userValidation
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidation = userValidation;
    }

    public List<UserDto> getAll() {
        return userMapper.toDtos(userRepository.getAll());
    }

    public UserDto create(UserDto userDto) {
        userValidation.validateCreation(userDto);
        Long userId = userRepository.add(userMapper.toObject(userDto));
        userDto.setId(userId);
        return userDto;
    }

    public UserDto update(Long id, UserDto userDto) {
        userDto.setId(id);
        userValidation.validateUpdate(userDto);
        User updatedUser;
        if (userDto.getEmail() != null && userDto.getName() != null) {
            User user = userMapper.toObject(userDto);
            updatedUser = userRepository.updateUser(user);
        } else {
            if (userDto.getEmail() != null) {
                userRepository.updateEmail(id, userDto.getEmail());
            } else {
                userRepository.updateName(id, userDto.getName());
            }
            updatedUser = getById(id);
        }
        return userMapper.toDto(updatedUser);
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
