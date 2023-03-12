package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.exeptions.EnumConverterException;

public class StringToBookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EnumConverterException("Unknown state: " + state);
        }
    }
}