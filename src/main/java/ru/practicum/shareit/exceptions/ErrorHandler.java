package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.models.ErrorMessage;
import ru.practicum.shareit.exceptions.models.ErrorResponse;
import ru.practicum.shareit.exceptions.models.Violation;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) {
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BookingException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handle(final UnsupportedBookingStateException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final RequestParamException e) {
        return new ErrorResponse(
                "Некорректный запрос:", e.getMessage()
        );
    }
}

