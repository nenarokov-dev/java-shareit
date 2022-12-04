package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @Email(message = "Введённая строка не обладает структурой email [***@**.**]")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    private String email;

}
