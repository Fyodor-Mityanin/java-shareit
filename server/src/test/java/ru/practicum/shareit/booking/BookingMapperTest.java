package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.ObjectMaker.*;

@SpringBootTest
public class BookingMapperTest {

    @Test
    public void dtoToObjectTest() {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.plusDays(1), now.plusDays(2));
        User owner = makeUser(1L, "Пётр", "ivanov@mail.ru");
        User booker = makeUser(2L, "Иван", "petrov@mail.ru");
        Item item = makeItem(2L, "Итем","Описание", owner, true);

        Booking booking = BookingMapper.toObject(bookingRequestDto, item, booker, BookingStatus.WAITING);

        assertThat(booking)
                .hasFieldOrPropertyWithValue("startDate", now.plusDays(1))
                .hasFieldOrPropertyWithValue("endDate", now.plusDays(2))
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
    }

    @Test
    public void objectToDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = makeUser(1L, "Пётр", "ivanov@mail.ru");
        User booker = makeUser(2L, "Иван", "petrov@mail.ru");
        Item item = makeItem(1L, "Итем","Описание", owner, true);
        Booking booking = makeBooking(1L, start, end, item, booker, BookingStatus.WAITING);

        BookingDto bookingDto = BookingMapper.toDto(booking);

        assertThat(bookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
    }
}
