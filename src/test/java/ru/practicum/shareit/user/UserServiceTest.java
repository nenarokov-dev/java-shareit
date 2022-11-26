package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
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
public class UserServiceTest {

    UserDto userDto1 = UserDto
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
    UserDto userDto3 = UserDto
            .builder()
            .id(3L)
            .name("user333")
            .email("user333@yandex.ru")
            .build();

    UserDto userDtoCreated = UserDto
            .builder()
            .id(4L)
            .name("userAdd")
            .email("userAdd@yandex.ru")
            .build();

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setUsers(){
        userService.add(userDto1);
        userService.add(userDto2);
        userService.add(userDto3);
    }

    @Test
    void testCreate() {
        userService.add(userDtoCreated);
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(4));
        assertThat(users, equalTo(List.of(userDto1, userDto2, userDto3, userDtoCreated)));
    }

    @Test
    void testUpdate() {
        UserDto userUpdate = UserDto
                .builder()
                .name("userUpdated")
                .email("userUpdated@yandex.ru")
                .build();
        userService.update(userUpdate,3L);
        UserDto userAfterUpdateDto = UserDto.builder().id(3L)
                .name(userUpdate.getName()).email(userUpdate.getEmail()).build();
        List<UserDto> users = userService.getAll();
        assertThat(users.size(), equalTo(3));
        assertThat(users, equalTo(List.of(userDto1, userDto2, userAfterUpdateDto)));
        assertThrows(NotFoundException.class, () -> userService.update(userUpdate,5L));
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
