package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {

    private final String authenticationHeader = "X-Sharer-User-Id";

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("user111")
            .email("user111@yandex.ru")
            .build();

    private final UserDto userDto2 = UserDto
            .builder()
            .id(2L)
            .name("user222")
            .email("user222@yandex.ru")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("Шпора")
            .description("Содержит в себе шаблоны юнит- и мок-тестов.")
            .available(true)
            .ownerId(userDto2.getId())
            .build();

    private final ItemDto itemDtoNoRequest = ItemDto.builder()
            .id(2L)
            .name("Шпора2")
            .description("Содержит в себе дополнительные шаблоны юнит- и мок-тестов.")
            .available(true)
            .ownerId(userDto2.getId())
            .requestId(null)
            .build();

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private MockMvc mvc;


    @Test
    void addItemTest() throws Exception {
        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, itemDto.getOwnerId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void addCommentTest() throws Exception {
        Comment comment = Comment
                .builder()
                .id(1L)
                .author(UserMapper.toUser(userDto))
                .item(ItemMapper.fromItemDto(itemDto, UserMapper.toUser(userDto)))
                .text("Не помогло")
                .build();
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(CommentMapper.toCommentDto(comment));

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(CommentMapper.toCommentDto(comment)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, itemDto.getOwnerId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header(authenticationHeader, itemDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void getAllTest() throws Exception {
        when(itemService.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemDto, itemDtoNoRequest));

        mvc.perform(get("/items")
                        .header(authenticationHeader, itemDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[1].id", is(itemDtoNoRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemDtoNoRequest.getName())))
                .andExpect(jsonPath("$.[1].description", is(itemDtoNoRequest.getDescription())));
    }

    @Test
    void search() throws Exception {
        when(itemService.searchItems(anyString(), any(), any()))
                .thenReturn(List.of(itemDto, itemDtoNoRequest));

        mvc.perform(get("/items/search")
                        .header(authenticationHeader, itemDto.getOwnerId())
                        .param("text", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[1].id", is(itemDtoNoRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemDtoNoRequest.getName())))
                .andExpect(jsonPath("$.[1].description", is(itemDtoNoRequest.getDescription())));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, itemDto.getOwnerId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }
}