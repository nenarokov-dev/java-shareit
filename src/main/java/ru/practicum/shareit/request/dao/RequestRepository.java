package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_Id(Long requestorId);

    @Query("select r from ItemRequest r where r.requestor.id <>?1")
    List<ItemRequest> findAllWhereRequestor_IdNotEquals(Long requestorId);
}
