package ru.practicum.shareit.error.exeptions;

public class CommentImpossibleException extends RuntimeException {
    public CommentImpossibleException(String message) {
        super(message);
    }
}