package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private int counter = 1;
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(User user) {
        emailCheck(user);
        if (user.getId() != null) {
            if (userStorage.getUserStorage().containsKey(user.getId())) {
                user.setId(generateId());
            }
        } else {
            user.setId(generateId());
        }
        userStorage.getUserStorage().put(user.getId(), user);
        userStorage.getEmailStorage().add(user.getEmail());
        log.info("Пользователь id=" + user.getId() + " успешно добавлен.");
        return user;
    }

    @Override
    public User get(Integer userId) {
        try {
            User user = userStorage.getUserStorage().get(userId);
            log.info("Пользователь id=" + userId + " успешно получен.");
            return user;
        } catch (NullPointerException e) {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.getUserStorage().values());
    }

    @Override
    public User update(User userForUpdate) {
        try {
            User user = userStorage.getUserStorage().get(userForUpdate.getId());
            if (userForUpdate.getEmail() != null) {
                emailCheck(userForUpdate);
                userStorage.getEmailStorage().remove(user.getEmail());
                user.setEmail(userForUpdate.getEmail());
                userStorage.getEmailStorage().add(user.getEmail());
            }
            if (userForUpdate.getName() != null) {
                user.setName(userForUpdate.getName());
            }
            log.info("Пользователь id=" + userForUpdate.getId() + " успешно получен.");
            return user;
        } catch (NullPointerException e) {
            String message = "Пользователь с id=" + userForUpdate.getId() + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public String delete(Integer userId) {
        User user = userStorage.getUserStorage().get(userId);
        userStorage.getEmailStorage().remove(user.getEmail());
        userStorage.getUserStorage().remove(userId);
        String message = "Пользователь id=" + userId + " был успешно удалён.";
        log.info(message);
        return message;
    }

    private int generateId() {
        return counter++;
    }

    private void emailCheck(User user) {
        if (userStorage.getEmailStorage().contains(user.getEmail())) {
            String message = "Пользователь с таким адресом электронной почты уже существует.";
            log.warn(message);
            throw new EmailException(message);
        }
    }
}
