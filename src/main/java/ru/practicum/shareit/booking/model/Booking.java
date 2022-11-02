package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Integer broker;
    private BookingStatus status;

}
