package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {

    private Integer id;
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @Email(message = "Введённая строка не обладает структурой email [***@**.**]")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    private String email;
}
