package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("/users findAll");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getOneById(@PathVariable long id) {
        log.info("/users/" + id + " getOneById");
        return userService.getById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("/users create: " + userDto.toString());
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patch(@Valid @RequestBody UserDto userDto, @PathVariable long id) {
        log.info("/users/" + id + " patch: " + userDto.toString());
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public String deleteOneById(@PathVariable long id) {
        log.info("/users/" + id + " deleteOneById");
        userService.delete(id);
        return id + "deleted";
    }
}