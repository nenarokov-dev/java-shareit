package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final String authenticationHeader = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public ItemRequest add(@RequestBody @Valid ItemRequestDto itemRequest,
                           @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return requestService.add(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequest> getAllByRequestor(
            @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return requestService.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllYouCanHelp(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return requestService.getAllYouCanHelp(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequest getByRequestId(@PathVariable Long requestId,
                                      @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return requestService.getByRequestId(requestId,userId);
    }


}
