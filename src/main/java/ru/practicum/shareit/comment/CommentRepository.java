package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long> {

    @Query("select new ru.practicum.shareit.comment.dto.CommentDto(c.id, c.text, c.author.name, c.created) " +
            "from Comment c " +
            "where c.item.id = :id")
    List<CommentDto> findByItemIdWithAuthor(@NonNull Long id);
}
