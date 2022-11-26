package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RequestService {
    private final RequestRepository requestStorage;
    private final ItemRepository itemRepository;

    private final ItemServiceImpl itemService;

    private final UserRepository userRepository;

    private final Pagination<ItemRequest> pagination;

    public ItemRequest add(ItemRequestDto itemRequest, Long userId) {
        isUserExistsCheck(userId);
        User user = userRepository.getReferenceById(userId);
        ItemRequest request = requestStorage.save(RequestMapper.fromRequestDto(itemRequest, user));
        log.info("Запрос на аренду id=" + itemRequest.getId() + " успешно добавлен.");
        return request;
    }

    public List<ItemRequest> getAllByRequestor(Long userId) {
        isUserExistsCheck(userId);
        List<ItemRequest> itemRequests = requestStorage.findAllByRequestor_Id(userId);
        itemRequests.forEach(e -> e.getItems().addAll(getItemDtoByRequest(e.getId())));
        log.info("Список запросов на аренду, созданных пользователем id=" + userId + " успешно получен.");
        return itemRequests;
    }

    public ItemRequest getByRequestId(Long requestId, Long userId) {
        isUserExistsCheck(userId);
        isRequestExistsCheck(requestId);
        ItemRequest itemRequest = requestStorage.findById(requestId).get();
        itemRequest.getItems().addAll(getItemDtoByRequest(requestId));
        log.info("Запрос на аренду предмета id=" + requestId + " успешно получен.");
        return itemRequest;
    }

    public List<ItemRequest> getAllYouCanHelp(Long userId, Integer from, Integer size) {
        List<ItemRequest> itemRequests = requestStorage.findAllWhereRequestor_IdNotEquals(userId);
        itemRequests.forEach(e -> e.getItems().addAll(getItemDtoByRequest(e.getId())));
        List<ItemRequest> itemRequestsPageable = pagination.setPagination(from, size, itemRequests);
        log.info("Список запросов на аренду, которым можно помочь, успешно получен.");
        return itemRequestsPageable;
    }

    private List<ItemDto> getItemDtoByRequest(Long requestId) {
        List<Item> items = itemRepository.findAllByItemRequest_Id(requestId);

        if (!items.isEmpty()) {
            return items
                    .stream()
                    .map(e -> itemService.itemDtoCreator(e, e.getOwner().getId()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private void isUserExistsCheck(Long userId) {

        if (userRepository.findById(userId).isEmpty()) {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void isRequestExistsCheck(Long requestId) {

        if (requestStorage.findById(requestId).isEmpty()) {
            String message = "Запрос на аренду с id=" + requestId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }
}
