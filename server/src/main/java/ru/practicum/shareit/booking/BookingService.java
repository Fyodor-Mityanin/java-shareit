package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getOneByIdAndUserId(Long bookingId, Long userId);

    List<BookingDto> getAllByBookerAndState(Long userId, BookingState state, Pageable pageable);

    List<BookingDto> getAllByOwnerAndState(Long userId, BookingState state, Pageable pageable);
}
