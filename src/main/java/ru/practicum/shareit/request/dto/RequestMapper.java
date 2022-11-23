package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;

@Component
@AllArgsConstructor
public class RequestMapper {

    private final UserRepository userRepository;

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest fromRequestDto(ItemRequestDto itemRequestDto, Long userId) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(userRepository.getReferenceById(userId))
                .build();
    }


}
