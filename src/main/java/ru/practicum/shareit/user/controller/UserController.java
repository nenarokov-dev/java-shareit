package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public UserDto add(@RequestBody @Valid UserDto user) {
        return userService.add(user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userForUpdate, @PathVariable Long userId) {
        return userService.update(userForUpdate,userId);
    }

    @DeleteMapping("/{userId}")
    public String delete(@PathVariable Long userId) {
        return userService.delete(userId);
    }

}
