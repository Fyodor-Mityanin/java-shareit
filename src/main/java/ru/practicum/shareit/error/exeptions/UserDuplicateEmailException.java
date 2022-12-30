package ru.practicum.shareit.error.exeptions;

public class UserDuplicateEmailException extends RuntimeException {
    public UserDuplicateEmailException(String message) {
        super(message);
    }
}