package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemService;

    @PostMapping
    public Item add(@RequestBody @Valid Item item, @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userId) {
        return itemService.add(item, userId);
    }

    @GetMapping("/{itemId}")
    public Item get(@PathVariable Integer itemId) {
        return itemService.get(itemId);
    }

    @GetMapping()
    public List<Item> getByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userId) {
        if (userId != null) {
            return itemService.getByOwner(userId);
        } else {
            return itemService.getAll();
        }
    }

    @GetMapping("/search")
    public List<Item> getByOwner(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestBody Item item, @PathVariable Integer itemId,
                       @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userId) {
        return itemService.update(item, userId, itemId);
    }
}
