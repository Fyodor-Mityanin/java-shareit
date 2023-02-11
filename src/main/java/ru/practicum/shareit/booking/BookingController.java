package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam @NotBlank Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneByIdAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getOneByIdAndUserId(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllByOwnerAndState(userId, state);
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllByBookerAndState(userId, state);
    }
}
