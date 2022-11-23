package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final String authenticationHeader = "X-Sharer-User-Id";
    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDto add(@RequestBody @Valid BookingDtoInput bookingDto,
                          @RequestHeader(value = authenticationHeader, required = false) Long userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestParam Boolean approved,
                              @RequestHeader(value = authenticationHeader, required = false) Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto get(@RequestHeader(value = authenticationHeader, required = false) Long userId,
                          @PathVariable Long bookingId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size,
                                           @RequestHeader(value = authenticationHeader, required = false) Long userId,
                                           @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByBooker(userId, state,from,size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestParam(required = false) Integer from,
                                          @RequestParam(required = false) Integer size,
                                          @RequestHeader(value = authenticationHeader, required = false) Long userId,
                                          @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(userId, state,from,size);
    }

}
