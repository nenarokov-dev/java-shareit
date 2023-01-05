package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Введённая строка не обладает структурой email [***@**.**]")
    private String email;
}
