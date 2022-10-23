package ru.practicum.shareit.user.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Set;

@Component
@AllArgsConstructor
@Data
public class UserStorage {

    private final HashMap<Integer, User> userStorage;
    private final Set<String> emailStorage;
}
