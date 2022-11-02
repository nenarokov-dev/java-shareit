package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.HeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private int counter = 1;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    @Override
    public Item add(Item item, Integer owner) {
        ownerCheck(owner);
        item.setOwner(owner);
        if (item.getId() != null) {
            if (itemStorage.getItemStorage().containsKey(item.getId())) {
                item.setId(generateId());
            }
        } else {
            item.setId(generateId());
        }
        itemStorage.getItemStorage().put(item.getId(), item);
        if (itemStorage.getUserItemsId().containsKey(owner)) {
            itemStorage.getUserItemsId().get(owner).add(item.getId());
        } else {
            itemStorage.getUserItemsId().put(owner, new HashSet<>());
            itemStorage.getUserItemsId().get(owner).add(item.getId());
        }
        log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно добавлен(-а).");
        return item;
    }

    @Override
    public Item get(Integer itemId) {
        try {
            Item item = itemStorage.getItemStorage().get(itemId);
            log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно получен(-а).");
            return item;
        } catch (NullPointerException e) {
            String message = "Предмет с id=" + itemId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Item update(Item item, Integer owner, Integer itemId) {
        ownerCheck(owner);
        if (!itemStorage.getUserItemsId().containsKey(owner)) {
            String message = "Пользователь с id=" + owner + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
        try {
            Item itemForUpdate = itemStorage.getItemStorage().get(itemId);
            if (!itemStorage.getUserItemsId().get(owner).contains(itemId)) {
                String message = "Ошибка.Редактировать предмет может только его владелец.";
                log.warn(message);
                throw new AuthorizationException(message);
            }
            if (item.getName() != null) {
                if (!item.getName().isBlank()) {
                    itemForUpdate.setName(item.getName());
                }
            }
            if (item.getDescription() != null) {
                if (!item.getDescription().isEmpty()) {
                    itemForUpdate.setDescription(item.getDescription());
                }
            }
            if (item.getAvailable() != null) {
                itemForUpdate.setAvailable(item.getAvailable());
            }
            log.info("Предмет '" + itemForUpdate.getName() + "' id=" + itemForUpdate.getId() + " был успешно обновлен.");
            return itemForUpdate;
        } catch (NullPointerException e) {
            String message = "Предмет с id=" + itemId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<Item> getAll() {
        log.info("Список всех имеющихся в базе предметов был успешно получен.");
        return new ArrayList<>(itemStorage.getItemStorage().values());
    }

    @Override
    public List<Item> getByOwner(Integer ownerId) {
        List<Item> items = new ArrayList<>(itemStorage.getItemStorage().values());
        log.info("Список предметов пользователя id=" + ownerId + " был успешно получен.");
        return items.stream().filter(i -> itemStorage.getUserItemsId().get(ownerId).contains(i.getId())).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (!text.isBlank()) {
            List<Item> items = new ArrayList<>(itemStorage.getItemStorage().values()).stream()
                    .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
            log.info("Получен поисковый запрос '" + text + "'. Список из " + items.size() + " предметов был отправлен .");
            return items;
        } else {
            log.info("Получен пустой поисковый запрос. Был отправлен пустой список.");
            return Collections.emptyList();
        }
    }

    private int generateId() {
        return counter++;
    }

    private void ownerCheck(Integer owner) {
        if (owner == null) {
            String message = "Не удалось добавить предмет.Отсутствует заголовок-авторизация.";
            log.warn(message);
            throw new HeaderException(message);
        } else if (!userStorage.getUserStorage().containsKey(owner)) {
            String message = "Пользователь с id=" + owner + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }
}
