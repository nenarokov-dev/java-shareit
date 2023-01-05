package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDtoOutput extends BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDtoOutput.Item item;
    private BookingDtoOutput.User booker;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String name;
    }
}
