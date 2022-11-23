package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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
    private final String authenticationHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestBody @Valid ItemDto item, @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return itemService.add(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto add(@RequestBody @Valid Comment comment,
                          @RequestHeader(value = authenticationHeader, required = false) Long userId,
                          @PathVariable Long itemId) {
        return itemService.addComment(comment, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId,
                       @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getAll(@RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size,
                                @RequestHeader(value = authenticationHeader, required = false) Long userId) {

        return itemService.getAll(userId,from,size);

    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(required = false) Integer from,
                                 @RequestParam(required = false) Integer size,
                                 @RequestParam String text) {
        return itemService.searchItems(text,from,size);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestBody Item item, @PathVariable Long itemId,
                       @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return itemService.update(item, userId, itemId);
    }
}
