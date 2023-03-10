package ru.practicum.shareit.error.exeptions;

public class BookingPermissionDeniedException extends RuntimeException {
    public BookingPermissionDeniedException(String message) {
        super(message);
    }
}