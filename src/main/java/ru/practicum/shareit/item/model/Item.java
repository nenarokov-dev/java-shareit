package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описание предмета не должно быть пустым.")
    private String description;
    @Column(name = "available", nullable = false)
    @NotNull(message = "При добавлении предмета необходимо указать статус его доступности.")
    private Boolean available;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

}
