package ru.practicum.shareit.exceptions;

public class RequestParamException extends RuntimeException {
    public RequestParamException(String message) {
        super(message);
    }
}
