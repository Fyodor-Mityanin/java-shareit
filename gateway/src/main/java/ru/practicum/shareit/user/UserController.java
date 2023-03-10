package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
//
//    @GetMapping("/{id}")
//    public User getOneById(@PathVariable long id) {
//        return userService.getById(id);
//    }
//
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }
//
//    @PatchMapping("/{id}")
//    public UserDto patch(@Valid @RequestBody UserDto userDto, @PathVariable long id) {
//        return userService.update(id, userDto);
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteOneById(@PathVariable long id) {
//        userService.delete(id);
//        return id + "deleted";
//    }
}
