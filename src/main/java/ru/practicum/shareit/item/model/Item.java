package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

    private Integer id;
    @NotBlank(message = "Название предмета не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым.")
    private String description;
    @NotNull(message = "При добавлении предмета необходимо указать статус его доступности.")
    private Boolean available;
    private Integer owner;
    private Integer request;
}
