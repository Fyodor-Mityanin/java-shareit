package ru.practicum.shareit.error.exeptions;

public class BookingImpossibleException extends RuntimeException {
    public BookingImpossibleException(String message) {
        super(message);
    }
}