package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.ObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl service;

    @Test
    @DisplayName("JUnit test for create Booking method")
    void givenUserDtoObject_whenSaveUserDto_thenReturnUserObject() {
        //given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = makeUser(1L, "Пётр", "ivanov@mail.ru");
        User booker = makeUser(2L, "Иван", "petrov@mail.ru");
        Item item = makeItem(1L, "Итем","Описание", owner, true);
        Booking booking = makeBooking(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(1, start, end);


        Mockito
                .when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(repository.save(BookingMapper.toObject(bookingRequestDto, item, booker, BookingStatus.WAITING)))
                .thenReturn(booking);


        // when
        BookingDto savedBookingDto = service.create(bookingRequestDto, booker.getId());

        // then - verify the output
        assertThat(savedBookingDto.getId(), notNullValue());
        assertThat(savedBookingDto.getEnd(), equalTo(booking.getEndDate()));
        assertThat(savedBookingDto.getStart(), equalTo(booking.getStartDate()));
        assertThat(savedBookingDto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(savedBookingDto.getStatus(), equalTo(booking.getStatus()));
    }
}