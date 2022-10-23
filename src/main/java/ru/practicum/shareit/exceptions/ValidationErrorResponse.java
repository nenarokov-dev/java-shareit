package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exceptions.models.Violation;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<Violation> violations;
}
