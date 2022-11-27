package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.models.ErrorMessage;
import ru.practicum.shareit.exceptions.models.ErrorResponse;
import ru.practicum.shareit.exceptions.models.Violation;

import java.util.List;

@SpringBootTest
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();


    @Test
    void testHandle() {
        Violation violation = new Violation("Привет", "Ревьюер!");

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(List.of(violation));

        ErrorResponse errorResponse = new ErrorResponse("Этот тест", "Только для покрытия)");

        ErrorMessage errorMessage = new ErrorMessage("памагити");

        errorHandler.handle(new NotFoundException(""));
        errorHandler.handle(new AuthorizationException(""));
        errorHandler.handle(new BookingException(""));
        errorHandler.handle(new UnsupportedBookingStateException(""));
        errorHandler.handle(new RequestParamException(""));
    }
}