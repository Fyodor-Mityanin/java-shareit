package ru.practicum.shareit.error.exeptions;

public class ItemOwnershipException extends RuntimeException {
    public ItemOwnershipException(String message) {
        super(message);
    }
}