package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым.")
    private String description;
    @NotNull(message = "При добавлении предмета необходимо указать статус его доступности.")
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}
