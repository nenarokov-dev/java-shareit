package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    UserDto userDto1 =
            UserDto.builder().id(1L).name("user111").build();
    UserDto userDto2 =
            UserDto.builder().id(2L).name("user222").build();
    UserDto userDto3 =
            UserDto.builder().id(3L).name("user333").build();

    User userAdd =
            User.builder().id(4L).name("userAdd").email("userAdd@yandex.ru").build();
    UserDto userDtoCreated =
            UserDto.builder().id(4L).name("userAdd").build();

    User user3 = new User(4L, "user333", "user333@yandex.ru");
    @Autowired
    private UserServiceImpl userService;

    @Test
    void testCreate() {
        userService.add(userAdd);
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(4));
        assertThat(users, equalTo(List.of(userDto1, userDto2, userDto3, userDtoCreated)));
    }

    @Test
    void testUpdate() {
        User userUpdate =
                User.builder().name("userUpdated").email("userUpdated@yandex.ru").build();
        userService.update(userUpdate,3L);
        UserDto userAfterUpdateDto = UserDto.builder().id(3L).name("userUpdated").build();
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(3));
        assertThat(users, equalTo(List.of(userDto1, userDto2, userAfterUpdateDto)));
        assertThrows(NotFoundException.class, () -> userService.get(5L));
    }

    @Test
    void testGet() {
        UserDto user = userService.get(3L);
        assertThat(user, equalTo(userDto3));
        assertThrows(NotFoundException.class, () -> userService.get(5L));
    }

    @Test
    void testGetAll() {
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(3));
        assertThat(users, equalTo(List.of(userDto1, userDto2, userDto3)));
    }

    @Test
    void testDelete() {
        userService.delete(1L);
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(2));
        assertThat(users, equalTo(List.of(userDto2, userDto3)));
    }
}