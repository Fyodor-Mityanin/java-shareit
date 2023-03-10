package ru.practicum.shareit.error.exeptions;

public class PaginationValidationException extends RuntimeException {
    public PaginationValidationException(String message) {
        super(message);
    }
}