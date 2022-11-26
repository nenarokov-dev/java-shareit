package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.controller.UserController;

@WebMvcTest(controllers = UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {

    @Test
    void add() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void get() {
    }

    @Test
    void getAll() {
    }

    @Test
    void search() {
    }

    @Test
    void update() {
    }
}