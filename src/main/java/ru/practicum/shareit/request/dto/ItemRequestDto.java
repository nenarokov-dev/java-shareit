package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@Builder
@Data
public class ItemRequestDto {

    private Long id;
    @NotEmpty(message = "Описание запроса не должно быть пустым.")
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private final List<ItemDto> items = new ArrayList<>();

}
