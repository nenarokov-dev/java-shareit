package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String authenticationHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(
            @RequestHeader(authenticationHeader) long userId,
            @RequestParam(defaultValue = "all") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByBooker(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader(authenticationHeader) long userId,
            @RequestParam(defaultValue = "all") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(authenticationHeader) long userId,
                                           @RequestBody @Valid BookingDtoInput booking) {
        log.info("Creating booking id={}, userId={}", booking, userId);
        return bookingClient.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestParam Boolean approved,
                                          @RequestHeader(authenticationHeader) long userId,
                                          @PathVariable Long bookingId) {
        log.info("Approve booking id={}", bookingId);
        return bookingClient.approve(approved, userId, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(authenticationHeader) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking id={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
