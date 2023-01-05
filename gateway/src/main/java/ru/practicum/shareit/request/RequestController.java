package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;
    private final String authenticationHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader(authenticationHeader) long userId) {
        log.info("Get requests by userId={}", userId);
        return requestClient.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllYouCanHelp(
            @RequestHeader(authenticationHeader) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests by userId={} with from={}, size={}", userId, from, size);
        return requestClient.getAllYouCanHelp(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(authenticationHeader) long userId,
                                             @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Creating request id={}, userId={}", requestDto, userId);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@RequestHeader(authenticationHeader) long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get request id={}, userId={}", requestId, userId);
        return requestClient.getByRequestId(userId, requestId);
    }
}
