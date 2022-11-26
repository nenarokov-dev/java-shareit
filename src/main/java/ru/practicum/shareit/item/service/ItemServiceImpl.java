package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;
    private final Pagination<Item> pagination;

    public ItemDto add(ItemDto itemForSave, Long owner) {
        isUserExistsCheck(owner);
        if (itemForSave.getRequestId() != null) {
            isRequestExistsCheck(itemForSave.getRequestId());
            Item item = itemRepository.save(ItemMapper.fromItemDto(
                    itemForSave,
                    userRepository.getReferenceById(owner),
                    requestRepository.getReferenceById(itemForSave.getRequestId())));
            log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно добавлен(-а).");
            return ItemMapper.toItemDto(item,item.getItemRequest());
        } else {
            Item item = itemRepository.save(ItemMapper.fromItemDto(itemForSave,
                    userRepository.getReferenceById(owner)));
            log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно добавлен(-а).");
            return itemDtoCreator(item, owner);
        }
    }

    public ItemDto get(Long itemId, Long userId) {
        isUserExistsCheck(userId);
        isItemExistsCheck(itemId);
        ItemDto item = itemDtoCreator(itemRepository.findById(itemId).get(), userId);
        log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно получен(-а).");
        return item;
    }

    public List<ItemDto> getAll(Long userId,Integer from,Integer size) {
        isUserExistsCheck(userId);
        List<ItemDto> items = pagination.setPagination(from,size,itemRepository.findAllByOwner_Id(userId))
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(i -> itemDtoCreator(i, userId))
                .collect(Collectors.toList());
        log.info("Пользователь id=" + userId + " получил список принадлежащих ему предметов.");
        return items;
    }

    public ItemDto update(ItemDto itemForUpdate, Long owner, Long itemId) {
        isUserExistsCheck(owner);
        isItemExistsCheck(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        log.error(item.toString());
        if (!item.getOwner().getId().equals(owner)) {
            String message = "Ошибка.Редактировать предмет может только его владелец.";
            log.warn(message);
            throw new AuthorizationException(message);
        }
        if (itemForUpdate.getName() != null) {
            item.setName(itemForUpdate.getName());
            log.error(item.toString());
        }
        if (itemForUpdate.getDescription() != null) {
            item.setDescription(itemForUpdate.getDescription());
            log.error(item.toString());
        }
        if (itemForUpdate.getAvailable() != null) {
            item.setAvailable(itemForUpdate.getAvailable());
            log.error(item.toString());
        }
        log.info("Предмет '" + itemForUpdate.getName() + "' id=" + itemForUpdate.getId() +
                " был успешно обновлен.");
        return itemDtoCreator(itemRepository.save(item),owner);
    }

    public List<ItemDto> searchItems(String text,Integer from,Integer size) {
        if (!text.isBlank()) {
            List<ItemDto> items = pagination.setPagination(from,size,itemRepository.search(text)).stream()
                    .map(e->itemDtoCreator(e, e.getOwner().getId()))
                    .collect(Collectors.toList());
            log.info("Получен поисковый запрос '" + text + "'. Список из " + items.size() + " предметов был отправлен .");
            return items;
        } else {
            log.info("Получен пустой поисковый запрос. Был отправлен пустой список.");
            return Collections.emptyList();
        }
    }

    public CommentDto addComment(Comment comment, Long userId, Long itemId) {
        isUserExistsCheck(userId);
        isItemExistsCheck(itemId);
        if (itemRepository.getReferenceById(itemId).getOwner().getId().equals(userId)) {
            String message = "Пользователь не может оставлять комментарии к собственному предмету.";
            log.warn(message);
            throw new BookingException(message);
        }
        if (bookingRepository
                .findBookingsByItem_IdAndBooker_IdAndStatusOrderByStart(itemId, userId, BookingStatus.APPROVED)
                .stream()
                .noneMatch(i -> i.getStart().isBefore(LocalDateTime.now()))) {
            String message = "Пользователь id=" + userId + " ещё не брал предмет id=" + itemId +
                    " в аренду и не может оставлять комментарии к нему.";
            log.warn(message);
            throw new BookingException(message);
        }
        comment.setAuthor(userRepository.getReferenceById(userId));
        comment.setItem(itemRepository.getReferenceById(itemId));
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Получен пустой поисковый запрос. Был отправлен пустой список.");
        return CommentMapper.toCommentDto(comment);

    }

    public ItemDto itemDtoCreator(Item item, Long userId) {
        List<Booking> itemApprovedBookings = bookingRepository
                .findBookingsByItem_IdAndStatusOrderByStart(item.getId(), BookingStatus.APPROVED)
                .stream()
                .filter(i -> !i.getBooker().getId().equals(userId))
                .collect(Collectors.toList());
        List<Booking> lastBookings = itemApprovedBookings.stream()
                .filter(i -> i.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        List<Booking> nextBookings = itemApprovedBookings.stream()
                .filter(i -> i.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        List<Booking> bookings = new ArrayList<>();
        if (!lastBookings.isEmpty()) {
            bookings.add(lastBookings.get(lastBookings.size() - 1));
        } else {
            bookings.add(null);
        }
        if (!nextBookings.isEmpty()) {
            bookings.add(nextBookings.get(0));
        } else {
            bookings.add(null);
        }
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemDto itemDto = ItemMapper.toItemDto(item, bookings);
        if (!comments.isEmpty()) {
            itemDto.setComments(comments);
        }
        if (item.getItemRequest()!=null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }

    private void isUserExistsCheck(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void isItemExistsCheck(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = "Предмет с id=" + itemId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void isRequestExistsCheck(Long requestId) {
        if (requestRepository.findById(requestId).isEmpty()) {
            String message = "Некорректный id запроса. Запрос с таким id не существует.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }
}
