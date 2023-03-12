package ru.practicum.shareit.error.exeptions;

public class CommentIsEmptyException extends RuntimeException {
    public CommentIsEmptyException(String message) {
        super(message);
    }
}