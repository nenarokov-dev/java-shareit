package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public class ItemMapper {

    public static ItemDto toItemDto(Item item, List<Booking> bookings) {
        if (bookings.get(1) == null) {
            if (bookings.get(0) != null) {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .lastBooking(new ItemDto.Booking(bookings.get(0).getId(),
                                bookings.get(0).getBooker().getId()))
                        .build();
            } else {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .build();
            }
        } else {
            if (bookings.get(0) != null) {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .lastBooking(new ItemDto.Booking(bookings.get(0).getId(),
                                bookings.get(0).getBooker().getId()))
                        .nextBooking(new ItemDto.Booking(bookings.get(1).getId(),
                                bookings.get(1).getBooker().getId()))
                        .build();
            } else {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .nextBooking(new ItemDto.Booking(bookings.get(1).getId(),
                                bookings.get(1).getBooker().getId()))
                        .build();
            }
        }
    }
}
