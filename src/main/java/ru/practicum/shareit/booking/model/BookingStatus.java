package ru.practicum.shareit.booking.model;

/**
 * Статус бронирования
 */
public enum BookingStatus {
    WAITING, //новое бронирование, ожидает одобрения
    APPROVED, //подтверждено владельцем
    REJECTED, //отклонено владельцем
    CANCELED //отменено создателем
}
