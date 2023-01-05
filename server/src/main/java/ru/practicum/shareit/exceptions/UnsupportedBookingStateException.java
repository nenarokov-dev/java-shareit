package ru.practicum.shareit.exceptions;

public class UnsupportedBookingStateException extends RuntimeException {
    public UnsupportedBookingStateException(String message) {
        super(message);
    }
}
