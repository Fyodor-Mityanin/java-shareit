package ru.practicum.shareit.error.exeptions;

public class ItemValidationException extends RuntimeException {
    public ItemValidationException(String message) {
        super(message);
    }
}