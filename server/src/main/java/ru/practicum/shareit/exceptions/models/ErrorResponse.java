package ru.practicum.shareit.exceptions.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String description;
}
