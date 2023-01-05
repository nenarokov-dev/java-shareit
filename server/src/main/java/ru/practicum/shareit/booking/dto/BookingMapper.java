package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@AllArgsConstructor
public class BookingMapper {

    public static BookingDtoOutput toBookingDto(Booking booking) {
        return BookingDtoOutput.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingDtoOutput.Item(booking.getItem().getId(),
                        booking.getItem().getName(),
                        booking.getItem().getDescription(),
                        booking.getItem().getAvailable()))
                .booker(new BookingDtoOutput.User(booking.getBooker().getId(),
                        booking.getBooker().getName()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromBookingDto(BookingDtoInput booking, User booker, Item item) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

}
