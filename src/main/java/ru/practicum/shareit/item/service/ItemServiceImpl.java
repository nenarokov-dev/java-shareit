package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
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

    public Item add(Item itemForSave, Long owner) {

        isUserExistsCheck(owner);
        itemForSave.setOwner(userRepository.findById(owner).get());
        Item item = itemRepository.save(itemForSave);
        log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно добавлен(-а).");
        return item;
    }

    public ItemDto get(Long itemId, Long userId) {
        isUserExistsCheck(userId);
        isItemExistsCheck(itemId);
        ItemDto item = itemDtoCreator(itemRepository.getById(itemId), userId);
        log.info("Предмет '" + item.getName() + "' id=" + item.getId() + " был(-а) успешно получен(-а).");
        return item;
    }

    public List<ItemDto> getAll(Long userId) {
        isUserExistsCheck(userId);
        List<ItemDto> items = itemRepository.getByOwner(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(i -> itemDtoCreator(i, userId))
                .collect(Collectors.toList());
        log.info("Пользователь id=" + userId + " получил список принадлежащих ему предметов.");
        return items;
    }

    public Item update(Item itemForUpdate, Long owner, Long itemId) {
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
        return itemRepository.save(item);
    }

    public List<Item> searchItems(String text) {
        if (!text.isBlank()) {
            List<Item> items = itemRepository.search(text);
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
        if (bookingRepository.findAllBookingsByItemIdAndUserId(itemId, userId).stream()
                .noneMatch(i -> i.getStart().isBefore(LocalDateTime.now()))) {
            String message = "Пользователь id=" + userId + " ещё не брал предмет id=" + itemId + " в аренду и не может " +
                    "оставлять комментарии к нему.";
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

    private ItemDto itemDtoCreator(Item item, Long userId) {
        List<Booking> itemApprovedBookings = bookingRepository.findInBookingNowByItemId(item.getId()).stream()
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
        List<CommentDto> comments = commentRepository.getAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemDto itemDto = ItemMapper.toItemDto(item, bookings);
        if (!comments.isEmpty()) {
            itemDto.setComments(comments);
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
}
