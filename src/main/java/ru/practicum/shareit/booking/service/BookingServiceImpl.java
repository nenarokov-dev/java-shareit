package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedBookingStateException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Pagination<BookingDto> pagination;

    public BookingDtoOutput add(BookingDtoInput bookingDto, Long bookerId) {
        isUserExists(bookerId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            String message = "Начало аренды не может быть после её окончания.";
            log.warn(message);
            throw new BookingException(message);
        }
        isItemExists(bookingDto.getItemId());
        Booking booking = BookingMapper.fromBookingDto(bookingDto,
                userRepository.getReferenceById(bookerId),
                itemRepository.getReferenceById(bookingDto.getItemId()));
        if (booking.getItem().getOwner().getId().equals(bookerId)) {
            String message = "Владелец предмета не может арендовать собственные предметы.";
            log.warn(message);
            throw new NotFoundException(message);//тут тоже лучше было бы BookingException, но надо 404.
        }
        if (!booking.getItem().getAvailable()) {
            String message = "Предмет с id=" + booking.getItem().getId() + " не доступен для аренды.";
            log.warn(message);
            throw new BookingException(message);
        }
        log.info("Запрос на аренду предмета '{}' (id={}) был успешно добавлен!",
                booking.getItem().getName(), booking.getItem().getId());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDtoOutput approve(Long bookingId, Long ownerId, Boolean approve) {
        isUserExists(ownerId);
        isBookingExists(bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            String message = "Подтверждать или отклонять запросы на аренду предметов может только их владелец!";
            log.warn(message);
            throw new NotFoundException(message);//тут больше подошёл бы BookingException, но тесты просят код 404.
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            String message = "Запрос на аренду предмета уже был подтверждён.";
            log.warn(message);
            throw new BookingException(message);
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Запрос на аренду предмета '{}' (id={}) был успешно подтверждён владельцем!",
                    booking.getItem().getName(), booking.getItem().getId());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Запрос на аренду предмета '{}' (id={}) был отклонён владельцем!",
                    booking.getItem().getName(), booking.getItem().getId());
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDtoOutput get(Long bookingId, Long userId) {
        isUserExists(userId);
        isBookingExists(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getItem().getOwner().getId().equals(userId)) {
            log.info("Запрос на аренду предмета '{}' (id={}) был успешно получен.",
                    booking.getItem().getName(), booking.getItem().getId());
            return BookingMapper.toBookingDto(booking);
        } else if (booking.getBooker().getId().equals(userId)) {
            log.info("Запрос на аренду предмета '{}' (id={}) был успешно получен.",
                    booking.getItem().getName(), booking.getItem().getId());
            return BookingMapper.toBookingDto(booking);
        } else {
            String message =
                    "Просматривать запрос на аренду предмета может только автор запроса или владелец предмета!";
            log.warn(message);
            throw new NotFoundException(message);//надоело плодить эксепшены под тесты постмана
            //тут больше подойдёт ошибка авторизации или что-то в этом духе
        }
    }

    public List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        isUserExists(userId);
        return pagination.setPagination(from, size,
                sortByState(bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId), state));
    }

    public List<BookingDto> getAllByOwner(Long ownerId, String state, Integer from, Integer size) {
        isUserExists(ownerId);
        return pagination.setPagination(from, size,
                sortByState(bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(ownerId), state));
    }

    private List<BookingDto> sortByState(List<Booking> bookings, String state) {
        if ("ALL".equals(state)) {
            return bookings.stream()
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if ("CURRENT".equals(state)) {
            return filterByCurrentTime(bookings)
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if ("PAST".equals(state)) {
            return bookings.stream()
                    .filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if ("FUTURE".equals(state)) {
            return bookings.stream()
                    .filter(e -> e.getEnd().isAfter(LocalDateTime.now()))
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if ("WAITING".equals(state)) {
            return bookings.stream()
                    .filter(e -> e.getStatus().equals(BookingStatus.WAITING))
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if ("REJECTED".equals(state)) {
            return bookings.stream()
                    .filter(e -> e.getStatus().equals(BookingStatus.REJECTED))
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else {
            String message = "Поиск по запросу '" + state + "' не поддерживается.";
            log.warn(message);
            throw new UnsupportedBookingStateException("Unknown state: " + state);
        }
    }

    private void isUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void isItemExists(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = "Предмет с id=" + itemId + " не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void isBookingExists(Long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            String message = "Запрос с id=" + bookingId + " на аренду предмета не найден.";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private Stream<Booking> filterByCurrentTime(List<Booking> bookings) {
        return bookings.stream()
                .filter(e -> (e.getStart().isBefore(LocalDateTime.now())))
                .filter(e -> e.getEnd().isAfter(LocalDateTime.now()));
    }
}
