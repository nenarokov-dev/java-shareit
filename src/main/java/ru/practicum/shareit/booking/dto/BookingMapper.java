package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

@Component
@AllArgsConstructor
public class BookingMapper {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    public BookingDto toBookingDto(Booking booking) {
        return BookingDtoOutput.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingDtoOutput.Item(booking.getItem().getId()
                        , booking.getItem().getName()
                        , booking.getItem().getDescription()
                        , booking.getItem().getAvailable()))
                .booker(new BookingDtoOutput.User(booking.getBooker().getId()
                        , booking.getBooker().getName()))
                .status(booking.getStatus())
                .build();
    }

    public Booking fromBookingDto(BookingDtoInput booking, Long booker) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(userRepository.findById(booker).get())
                .item(itemRepository.findById(booking.getItemId()).get())
                .status(BookingStatus.WAITING)
                .build();
    }

}
