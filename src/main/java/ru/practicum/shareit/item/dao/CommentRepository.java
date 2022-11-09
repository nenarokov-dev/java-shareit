package ru.practicum.shareit.item.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(" select с from Comment с " +
            "where с.item.id=?1")
    List<Comment> getAllByItemId(Long itemId);

}