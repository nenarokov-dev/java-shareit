package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RequestParamException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestServiceTest {

    private final ItemRequestDto requestDto = ItemRequestDto
            .builder()
            .id(1L)
            .description("То что поможет прохождению тестов в проложении ShareItApp")
            .requestorId(1L)
            .build();

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("user111")
            .email("user111@yandex.ru")
            .build();

    private final UserDto userDtoForHelp = UserDto
            .builder()
            .id(2L)
            .name("user222")
            .email("user222@yandex.ru")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .requestId(1L)
            .available(true)
            .build();

    @Autowired
    private RequestService requestService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setRequestItem() {
        userService.add(userDto);
        userService.add(userDtoForHelp);
    }

    @Test
    void addRequestTest() {
        ItemRequest itemRequest = requestService.add(requestDto, userDto.getId());
        ItemRequest itemRequestConverted = RequestMapper.fromRequestDto(requestDto, UserMapper.toUser(userDto));
        itemRequestConverted.setCreated(itemRequest.getCreated());
        assertThat(itemRequest, equalTo(itemRequestConverted));
    }

    @Test
    void getAllByRequestorTest() {
        ItemRequest itemRequest = requestService.add(requestDto, userDto.getId());
        List<ItemRequest> requests = requestService.getAllByRequestor(userDto.getId());
        assertThat(requests, equalTo(List.of(itemRequest)));
    }

    @Test
    void getByRequestIdTest() {
        ItemRequest itemRequest = requestService.add(requestDto, userDto.getId());
        itemService.add(itemDto, userDtoForHelp.getId());
        ItemRequest itemRequestDto = requestService.getByRequestId(itemRequest.getId(), userDto.getId());
        assertThat(itemRequestDto, equalTo(itemRequest));
        assertThat(itemRequestDto.getRequestor().getId(), equalTo(requestDto.getRequestorId()));
    }

    @Test
    void getAllYouCanHelpTest() {
        ItemRequestDto requestDtoForCreate = ItemRequestDto
                .builder()
                .description("То что поможет прохождению тестов в проложении ShareItApp")
                .requestorId(1L)
                .build();
        requestService.add(requestDtoForCreate, userDto.getId());
        ItemRequest itemRequest1 = requestService.add(requestDtoForCreate, userDtoForHelp.getId());
        ItemRequest itemRequest2 = requestService.add(requestDtoForCreate, userDtoForHelp.getId());
        List<ItemRequest> requests = requestService.getAllYouCanHelp(userDto.getId(), null, null);
        assertThat(requests, equalTo(List.of(itemRequest1, itemRequest2)));
        assertThrows(RequestParamException.class,
                () -> requestService.getAllYouCanHelp(userDto.getId(), -100, null));
        assertThrows(RequestParamException.class,
                () -> requestService.getAllYouCanHelp(userDto.getId(), 0, -100));
    }

    @Test
    void notExistUserAndRequestTest() {
        assertThrows(NotFoundException.class, () -> requestService.add(requestDto, 100L));
        assertThrows(NotFoundException.class, () -> requestService.getByRequestId(100L, 1L));
    }
}
