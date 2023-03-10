package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private final Long itemId;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

}
