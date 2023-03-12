package ru.practicum.shareit.booking.dto;

import lombok.NonNull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exeptions.BookingValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toDto(@NonNull Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toObject(@NonNull BookingDto bookingDto, Item item, User booker) {
        LocalDateTime startTime = bookingDto.getStart();
        LocalDateTime endTime = bookingDto.getEnd();
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Начало в прошлом");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Конец в прошлом");
        }
        if (startTime.isAfter(endTime)) {
            throw new BookingValidationException("Конец раньше старта");
        }
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartDate(startTime);
        booking.setEndDate(endTime);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static Booking toObject(BookingRequestDto bookingRequestDto, Item item, User booker, BookingStatus status) {
        BookingDto bookingDto = BookingDto.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(status)
                .build();
        return toObject(bookingDto, item, booker);
    }

    public static List<BookingDto> toDtos(@NonNull List<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        bookings.forEach(booking -> dtos.add(toDto(booking)));
        return dtos;
    }
}
