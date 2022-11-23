package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описание требуемой вещи не должно быть пустым.")
    private String description;
    @OneToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @Column(name = "created", nullable = false)
    private final LocalDateTime created = LocalDateTime.now();
    @Transient
    private final List<ItemDto> items = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
