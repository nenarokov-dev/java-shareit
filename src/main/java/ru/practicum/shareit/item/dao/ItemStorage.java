package ru.practicum.shareit.item.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Set;

@Component
@AllArgsConstructor
@Data
public class ItemStorage {
    private final HashMap<Integer, Set<Integer>> userItemsId;
    private final HashMap<Integer, Item> itemStorage;
}
