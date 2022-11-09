package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Integer request;
    private ItemDto.Booking lastBooking;
    private ItemDto.Booking nextBooking;
    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
    }
}
