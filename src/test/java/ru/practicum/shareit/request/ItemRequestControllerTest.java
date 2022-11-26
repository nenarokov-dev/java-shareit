package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestControllerTest {

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

    UserDto userDtoForHelp = UserDto
            .builder()
            .id(2L)
            .name("user222")
            .email("user222@yandex.ru")
            .build();

    ItemDto itemDto = ItemDto.builder()
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .requestId(1L)
            .available(true)
            .build();

    ItemRequest request;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @MockBean
    UserServiceImpl userService;


    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setRequestItem() {
        userService.add(userDto);
        request = RequestMapper.fromRequestDto(requestDto, UserMapper.toUser(userDto));
        userService.add(userDtoForHelp);
    }

    @Test
    void addRequest() throws Exception {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestor(UserMapper.toUser(userDto))
                .description(itemDto.getDescription())
                .id(1L).build();

        when(requestService.add(any(), anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequest.getRequestor().getId()), Long.class));
    }

    @Test
    void getAllByRequestor() throws Exception {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .requestor(UserMapper.toUser(userDto))
                .description("1")
                .id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .requestor(UserMapper.toUser(userDto))
                .description("2")
                .id(2L).build();
        when(requestService.getAllByRequestor(anyLong()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",
                        is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(itemRequest1.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id",
                        is(itemRequest1.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[1].id",
                        is(itemRequest2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description",
                        is(itemRequest2.getDescription())))
                .andExpect(jsonPath("$.[1].requestor.id",
                        is(itemRequest2.getRequestor().getId()), Long.class));

    }

    @Test
    void getAllYouCanHelpTest() throws Exception {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .requestor(UserMapper.toUser(userDto))
                .description("1")
                .id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .requestor(UserMapper.toUser(userDto))
                .description("2")
                .id(2L).build();
        when(requestService.getAllYouCanHelp(anyLong(), any(), any()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",
                        is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(itemRequest1.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id",
                        is(itemRequest1.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[1].id",
                        is(itemRequest2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description",
                        is(itemRequest2.getDescription())))
                .andExpect(jsonPath("$.[1].requestor.id",
                        is(itemRequest2.getRequestor().getId()), Long.class));
    }

    @Test
    void getByRequestId() throws Exception {
        when(requestService.getByRequestId(anyLong(), anyLong()))
                .thenReturn(request);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(request.getRequestor().getId()), Long.class));
    }
}
