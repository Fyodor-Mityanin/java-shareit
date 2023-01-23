package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toDto(Comment comment, User user) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(user.getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> toDtos(List<Comment> comments, User user) {
        List<CommentDto> dtos = new ArrayList<>();
        comments.forEach(comment -> dtos.add(toDto(comment, user)));
        return dtos;
    }
}
