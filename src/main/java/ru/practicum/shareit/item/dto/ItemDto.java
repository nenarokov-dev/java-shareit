package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым.")
    private String description;
    @NotNull(message = "При добавлении предмета необходимо указать статус его доступности.")
    private Boolean available;
    private Long requestId;
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
