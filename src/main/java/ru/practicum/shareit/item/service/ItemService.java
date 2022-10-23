package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, Integer owner);

    Item get(Integer itemId);

    Item update(Item item, Integer owner, Integer itemId);

    List<Item> getAll();

    List<Item> getByOwner(Integer ownerId);

    List<Item> searchItems(String text);

}
