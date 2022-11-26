package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RequestParamException;
import ru.practicum.shareit.exceptions.UnsupportedBookingStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceImplTest {

    UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("user111")
            .email("user111@yandex.ru")
            .build();

    UserDto userDtoForHelp = UserDto
            .builder()
            .id(2L)
            .name("user222")
            .email("user222@yandex.ru")
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .available(true)
            .build();

    ItemDto itemDtoNotAvailable = ItemDto.builder()
            .id(2L)
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .available(false)
            .build();

    LocalDateTime testTime;

    BookingDtoInput bookingDto;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setData() {
        testTime = LocalDateTime.now();
        bookingDto = BookingDtoInput.builder()
                .id(1L)
                .itemId(itemDto.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
        userService.add(userDto);
        userService.add(userDtoForHelp);
        itemService.add(itemDto, userDto.getId());
        itemService.add(itemDtoNotAvailable, userDtoForHelp.getId());
    }

    @Test
    void addBookingTest() {
        BookingDtoOutput bookingDtoOutput = bookingService.add(bookingDto, userDtoForHelp.getId());
        assertThat(bookingDtoOutput.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoOutput.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(bookingDtoOutput.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoOutput.getEnd(), equalTo(bookingDto.getEnd()));
        assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingDto, userDto.getId()));
        assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingDto, 100L));
        bookingDto.setItemId(100L);
        assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingDto, userDtoForHelp.getId()));
        bookingDto.setEnd(testTime.minusMinutes(100));
        assertThrows(BookingException.class,
                () -> bookingService.add(bookingDto, userDtoForHelp.getId()));
        BookingDtoInput notAvailableBooking = BookingDtoInput.builder()
                .id(2L)
                .itemId(itemDtoNotAvailable.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
        assertThrows(BookingException.class,
                () -> bookingService.add(notAvailableBooking, userDto.getId()));
    }

    @Test
    void approveBookingTest() {
        BookingDtoOutput bookingDtoOutput = bookingService.add(bookingDto, userDtoForHelp.getId());
        BookingDtoOutput bookingDtoOutputAfterApprove = bookingService
                .approve(bookingDtoOutput.getId(), userDto.getId(), true);
        assertThat(bookingDtoOutputAfterApprove.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThrows(NotFoundException.class,
                () -> bookingService.approve(10L, userDto.getId(), true));
        assertThrows(NotFoundException.class,
                () -> bookingService.approve(bookingDtoOutput.getId(), userDtoForHelp.getId(), true));
        assertThrows(BookingException.class,
                () -> bookingService.approve(bookingDtoOutput.getId(), userDto.getId(), true));
        BookingDtoInput bookingForReject = BookingDtoInput.builder()
                .id(2L)
                .itemId(itemDto.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
        BookingDtoOutput bookingDtoForReject = bookingService.add(bookingForReject, userDtoForHelp.getId());
        BookingDtoOutput bookingDtoOutputAfterReject = bookingService
                .approve(bookingDtoForReject.getId(), userDto.getId(), false);
        assertThat(bookingDtoOutputAfterReject.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingByIdTest() {
        BookingDtoOutput bookingDtoOutput = bookingService.add(bookingDto, userDtoForHelp.getId());
        BookingDtoOutput bookingByOwner = bookingService.get(bookingDtoOutput.getId(), userDto.getId());
        BookingDtoOutput bookingByBooker = bookingService.get(bookingDtoOutput.getId(), userDtoForHelp.getId());
        assertThat(bookingByOwner, equalTo(bookingByBooker));
        assertThat(bookingDtoOutput, equalTo(bookingByBooker));
        UserDto userDtoForBooking = UserDto.builder().name("xxx").email("xxx@yandex.ru").build();
        userService.add(userDtoForBooking);
        assertThrows(NotFoundException.class,
                () -> bookingService.get(bookingDtoOutput.getId(), 3L));
    }

    @Test
    void getAllByBookerTest() {
        LocalDateTime timeTest = LocalDateTime.now();
        bookingDto.setStart(timeTest.plusMinutes(100));
        bookingDto.setEnd(timeTest.plusMinutes(200));
        BookingDtoOutput bookingDtoOutput = bookingService.add(bookingDto, userDtoForHelp.getId());
        List<BookingDto> bookingDtoOutputList1 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "ALL", null, null);
        assertThat(bookingDtoOutputList1, equalTo(List.of(bookingDtoOutput)));
        assertThrows(RequestParamException.class,
                () -> bookingService.getAllByBooker(userDtoForHelp.getId(), "ALL", -100, null));
        assertThrows(RequestParamException.class,
                () -> bookingService
                        .getAllByBooker(userDtoForHelp.getId(), "ALL", 0, -100));
        List<BookingDto> bookingDtoOutputList2 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "CURRENT", null, null);
        assertThat(bookingDtoOutputList2, equalTo(Collections.emptyList()));
        List<BookingDto> bookingDtoOutputList3 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "PAST", null, null);
        assertThat(bookingDtoOutputList3, equalTo(Collections.emptyList()));
        List<BookingDto> bookingDtoOutputList4 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "FUTURE", null, null);
        assertThat(bookingDtoOutputList4, equalTo(List.of(bookingDtoOutput)));
        List<BookingDto> bookingDtoOutputList5 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "WAITING", null, null);
        assertThat(bookingDtoOutputList5, equalTo(List.of(bookingDtoOutput)));
        List<BookingDto> bookingDtoOutputList6 = bookingService
                .getAllByBooker(userDtoForHelp.getId(), "REJECTED", null, null);
        assertThat(bookingDtoOutputList6, equalTo(Collections.emptyList()));
        assertThrows(UnsupportedBookingStateException.class,
                () -> bookingService.getAllByBooker(userDtoForHelp.getId(), "йожек", null, null));
    }

    @Test
    void getAllByOwner() {
        BookingDtoOutput bookingDtoOutput = bookingService.add(bookingDto, userDtoForHelp.getId());
        List<BookingDto> bookingDtoOutputList = bookingService
                .getAllByOwner(userDto.getId(), "ALL", null, null);
        assertThat(bookingDtoOutputList, equalTo(List.of(bookingDtoOutput)));
    }
}
