package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerTest {

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .available(true)
            .build();

    UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("user111")
            .email("user111@yandex.ru")
            .build();

    LocalDateTime testTime;

    BookingDtoInput bookingDto;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setData() {
        testTime = LocalDateTime.now();
        bookingDto = BookingDtoInput.builder()
                .id(1L)
                .itemId(itemDto.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
    }

    @Test
    void add() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        when(bookingService.add(any(), anyLong()))
                .thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void approve() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getTest() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getAllByBooker() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        when(bookingService.getAllByBooker(anyLong(), anyString(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBookingDto(booking)));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }

    @Test
    void getAllByOwner() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        when(bookingService.getAllByOwner(anyLong(), anyString(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBookingDto(booking)));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }
}