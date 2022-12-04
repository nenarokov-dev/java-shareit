package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoInput {

    private Long id;
    @FutureOrPresent(message = "Начало аренды не должно быть в прошлом.")
    private LocalDateTime start;
    @Future(message = "Завершение аренды не должно быть в прошлом.")
    private LocalDateTime end;
    private Long itemId;
}