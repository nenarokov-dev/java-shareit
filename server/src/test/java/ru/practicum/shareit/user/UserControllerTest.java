package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user111")
            .email("user111@yanex.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("user222")
            .email("user222@yanex.ru")
            .build();

    private final UserDto userDto3 = UserDto.builder()
            .id(3L)
            .name("user333")
            .email("user333@yanex.ru")
            .build();

    private final User user1 = User.builder()
            .id(1L)
            .name("user111")
            .email("user111@yanex.ru")
            .build();

    private final User userUpdate = User.builder()
            .id(1L)
            .name("userUpdate")
            .email("userUpdate@yanex.ru")
            .build();

    private final UserDto userUpdateDto = UserMapper.toUserDto(userUpdate);

    @BeforeEach
    void setUsers() {
        userService.add(userDto1);
        userService.add(userDto2);
        userService.add(userDto3);
    }

    @Test
    void addUser() throws Exception {
        when(userService.add(any()))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

    }

    @Test
    void getUserById() throws Exception {
        when(userService.get(any()))
                .thenReturn(userDto1);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto1, userDto2, userDto3));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$.[1].email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$.[2].id", is(userDto3.getId()), Long.class))
                .andExpect(jsonPath("$.[2].name", is(userDto3.getName())))
                .andExpect(jsonPath("$.[2].email", is(userDto3.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(any(), anyLong()))
                .thenReturn(userUpdateDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userUpdateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userUpdateDto.getName())))
                .andExpect(jsonPath("$.email", is(userUpdateDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/3"))
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1))
                .delete(anyLong());
    }
}
