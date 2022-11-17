package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl {

    private UserRepository userStorage;

    public User add(User user) {
        userStorage.save(user);
        log.info("Пользователь id=" + user.getId() + " успешно добавлен.");
        return user;
    }

    public User get(Long userId) {
        if (userStorage.findById(userId).isPresent()) {
            User user = userStorage.findById(userId).get();
            log.info("Пользователь id=" + userId + " успешно получен.");
            return user;
        } else {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    public List<User> getAll() {
        return new ArrayList<>(userStorage.findAll());
    }

    public User update(User userForUpdate) {
        Long userId = userForUpdate.getId();
        if (userStorage.findById(userId).isPresent()) {
            User user = userStorage.getReferenceById(userId);
            if (userForUpdate.getEmail() != null) {
                user.setEmail(userForUpdate.getEmail());
            }
            if (userForUpdate.getName() != null) {
                user.setName(userForUpdate.getName());
            }
            log.info("Пользователь id=" + userForUpdate.getId() + " успешно обновлен.");
            return userStorage.save(user);
        } else {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    public String delete(Long userId) {
        userStorage.deleteById(userId);
        String message = "Пользователь id=" + userId + " был успешно удалён.";
        log.info(message);
        return message;
    }

}
