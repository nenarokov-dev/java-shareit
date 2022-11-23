package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;
    @NotEmpty(message = "Описание запроса не должно быть пустым.")
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private final List<ItemDto> items;

}
