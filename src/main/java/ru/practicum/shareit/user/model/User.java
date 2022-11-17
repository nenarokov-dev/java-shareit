package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Введённая строка не обладает структурой email [***@**.**]")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    private String email;

}
