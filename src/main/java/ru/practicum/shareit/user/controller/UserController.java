package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
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
    public User add(@RequestBody @Valid User user) {
        return userService.add(user);
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Integer userId) {
        return userService.get(userId);
    }

    @GetMapping()
    public List<User> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody User userForUpdate, @PathVariable Integer userId) {
        userForUpdate.setId(userId);
        return userService.update(userForUpdate);
    }

    @DeleteMapping("/{userId}")
    public String delete(@PathVariable Integer userId) {
        return userService.delete(userId);
    }
}
