package ru.practicum.shareit.error.exeptions;

public class RequestIsEmptyException extends RuntimeException {
    public RequestIsEmptyException(String message) {
        super(message);
    }
}