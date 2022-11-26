package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RequestParamException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
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
class ItemServiceImplTest {

    ItemRequestDto requestDto = ItemRequestDto
            .builder()
            .id(1L)
            .description("То что поможет прохождению тестов в проложении ShareItApp")
            .requestorId(1L)
            .build();

    UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("user111")
            .email("user111@yandex.ru")
            .build();

    UserDto userDto2 = UserDto
            .builder()
            .id(2L)
            .name("user222")
            .email("user222@yandex.ru")
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .requestId(1L)
            .available(true)
            .ownerId(userDto2.getId())
            .build();

    ItemDto itemDtoNoRequest = ItemDto.builder()
            .id(2L)
            .name("Шпора2")
            .description("Содержит в себе дополнительные шаблоны юнит- и мок-тестов.")
            .available(true)
            .ownerId(userDto2.getId())
            .requestId(null)
            .build();

    ItemDto itemDtoWrongIdRequest = ItemDto.builder()
            .id(3L)
            .name("Шпора3")
            .description("Содержит в себе дополнительные шаблоны юнит- и мок-тестов.")
            .available(true)
            .requestId(100L)
            .build();
    @Autowired
    private RequestService requestService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setRequestItem() {
        UserDto user = userService.add(userDto);
        userService.add(userDto2);
        requestService.add(requestDto,user.getId());
    }

    @Test
    void addTest() {
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        assertThat(item, equalTo(itemDto));
        ItemDto item2 = itemService.add(itemDtoNoRequest,userDto2.getId());
        assertThat(item2, equalTo(itemDtoNoRequest));
        assertThrows(NotFoundException.class,
                () -> itemService.add(itemDtoWrongIdRequest,userDto2.getId()));
        assertThrows(NotFoundException.class,
                () -> itemService.add(itemDto, 4L));
    }

    @Test
    void getByIdTest() {
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        ItemDto item2 = itemService.get(item.getId(),userDto2.getId());
        assertThat(item2, equalTo(itemDto));
    }

    @Test
    void getAllTest() {
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        List<ItemDto> items = itemService.getAll(userDto2.getId(),null,null);
        assertThat(items, equalTo(List.of(item)));
        assertThrows(RequestParamException.class,
                () -> itemService.getAll(userDto2.getId(), -100, null));
        assertThrows(RequestParamException.class,
                () -> itemService.getAll(userDto2.getId(), 0, 0));
    }

    @Test
    void updateTest() {
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        ItemDto itemUpdated = itemService.update(itemDtoNoRequest,item.getOwnerId(),item.getId());
        ItemDto itemAfterUpdate = itemService.get(item.getId(),item.getOwnerId());
        assertThat(itemUpdated, equalTo(itemAfterUpdate));
        assertThrows(AuthorizationException.class,
                () -> itemService.update(itemDtoNoRequest,userDto.getId(),item.getId()));
        assertThrows(NotFoundException.class,
                () -> itemService.update(itemDtoNoRequest,100L,item.getId()));
        assertThrows(NotFoundException.class,
                () -> itemService.update(itemDtoNoRequest,userDto2.getId(),100L));
    }

    @Test
    void searchItemsTest() {
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        List<ItemDto> items = itemService.searchItems("тест",null,null);
        List<ItemDto> itemsEmpty = itemService.searchItems("",null,null);
        assertThat(items, equalTo(List.of(item)));
        assertThat(itemsEmpty, equalTo(Collections.emptyList()));
    }

    @Test
    void addCommentTest() throws InterruptedException {
        LocalDateTime testTime = LocalDateTime.now();
        ItemDto item = itemService.add(itemDto,userDto2.getId());
        BookingDtoInput lastBookingDto = BookingDtoInput.builder()
                .id(1L)
                .itemId(item.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
        BookingDtoInput nextBookingDto = BookingDtoInput.builder()
                .id(2L)
                .itemId(item.getId())
                .start(testTime.plusMinutes(1))
                .end(testTime.plusMinutes(2))
                .build();
        Comment comment = Comment.builder().text("Не помогло").build();
        assertThrows(BookingException.class,
                () -> itemService.addComment(comment,userDto2.getId(),item.getId()));
        assertThrows(BookingException.class,
                () -> itemService.addComment(comment,userDto.getId(),item.getId()));
        BookingDtoOutput bookingLast = bookingService.add(lastBookingDto,userDto.getId());
        bookingService.approve(bookingLast.getId(),item.getOwnerId(),true);
        Thread.sleep(2000);
        BookingDtoOutput bookingNext = bookingService.add(nextBookingDto,userDto.getId());
        bookingService.approve(bookingNext.getId(),item.getOwnerId(),true);
        CommentDto commentAdded = itemService.addComment(comment,userDto.getId(),item.getId());
        ItemDto itemWithBookingsAndComment = itemService.get(item.getId(),userDto2.getId());
        assertThat(itemWithBookingsAndComment.getComments(), equalTo(List.of(commentAdded)));
    }

}