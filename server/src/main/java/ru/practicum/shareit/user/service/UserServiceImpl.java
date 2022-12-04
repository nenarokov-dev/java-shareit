package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl {

    private UserRepository userStorage;

    public UserDto add(UserDto userDto) {
        User savedUser = userStorage.save(UserMapper.toUser(userDto));
        log.info("Пользователь id={} успешно добавлен.", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    public UserDto get(Long userId) {
        if (userStorage.findById(userId).isPresent()) {
            User user = userStorage.findById(userId).get();
            log.info("Пользователь id={} успешно получен.", userId);
            return UserMapper.toUserDto(user);
        } else {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    public List<UserDto> getAll() {
        List<UserDto> users = userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        log.info("Список пользователей успешно получен.");
        return users;
    }

    public UserDto update(UserDto userForUpdate, Long userId) {
        userForUpdate.setId(userId);
        if (userStorage.findById(userId).isPresent()) {
            User user = userStorage.getReferenceById(userId);
            if (userForUpdate.getEmail() != null) {
                user.setEmail(userForUpdate.getEmail());
            }
            if (userForUpdate.getName() != null) {
                user.setName(userForUpdate.getName());
            }
            log.info("Пользователь id={} успешно обновлен.", userForUpdate.getId());
            User savedUser = userStorage.save(user);
            return UserMapper.toUserDto(savedUser);
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
