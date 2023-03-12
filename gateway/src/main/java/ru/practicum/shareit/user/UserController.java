package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneById(@PathVariable long id) {
        log.info("Get user by id: id={}", id);
        return userClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Create user: userDto={}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@Valid @RequestBody UserDto userDto, @PathVariable long id) {
        log.info("Update user: userDto={}", userDto);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOneById(@PathVariable long id) {
        log.info("Delete user: id={}", id);
        return userClient.deleteOneById(id);
    }
}
