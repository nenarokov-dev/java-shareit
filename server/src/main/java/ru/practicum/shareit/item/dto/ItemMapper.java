package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
                        .ownerId(item.getOwner().getId())
                        .lastBooking(new ItemDto.Booking(bookings.get(0).getId(),
                                bookings.get(0).getBooker().getId()))
                        .build();
            } else {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwner().getId())
                        .build();
            }
        } else {
            if (bookings.get(0) != null) {
                return ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwner().getId())
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
                        .ownerId(item.getOwner().getId())
                        .nextBooking(new ItemDto.Booking(bookings.get(1).getId(),
                                bookings.get(1).getBooker().getId()))
                        .build();
            }
        }
    }

    /**
     * itemDto без букингов и с не null полем requestId
     */
    public static ItemDto toItemDto(Item item, ItemRequest itemRequest) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(itemRequest.getId())
                .build();


    }


    public static Item fromItemDto(ItemDto item, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequest(itemRequest)
                .owner(owner)
                .build();
    }

    public static Item fromItemDto(ItemDto item, User owner) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .build();
    }
}
