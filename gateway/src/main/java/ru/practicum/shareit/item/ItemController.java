package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private final String authenticationHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(authenticationHeader) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items by userId={} with from={}, size={}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearch(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items by tag={} with page from={}, size={}", text, from, size);
        return itemClient.getSearch(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(authenticationHeader) long userId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item name={} by userId={}", itemDto.getName(), userId);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Valid ItemDtoForUpdate itemDto,
                                         @RequestHeader(authenticationHeader) long userId,
                                         @PathVariable Long itemId) {
        log.info("Update item id={}", itemId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(authenticationHeader) long userId,
                                          @PathVariable Long itemId) {
        log.info("Get booking id={}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(authenticationHeader) long userId,
            @RequestBody @Valid CommentDto commentDto,
            @PathVariable Long itemId) {
        log.info("Creating new comment to itemId={} by userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
