package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NullPointerException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final EmailException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final HeaderException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(final AuthorizationException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }
}

@Getter
@RequiredArgsConstructor
class ErrorResponse {

    private final String error;
    private final String description;
}
