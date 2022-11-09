package ru.practicum.shareit.item.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) AND " +
            "i.available=true")
    List<Item> search(String text);

    @Query(" select i from Item i " +
            "where i.owner.id =?1")
    List<Item> getByOwner(Long ownerId);

    @Query(" select i from Item i " +
            "where i.id =?1")
    Item getById(Long itemId);

    @Query(" select i from Item i " +
            "order by i.id desc ")
    List<Item> getAll();

}