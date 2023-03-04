package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.ObjectMaker.*;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    BookingRepository repository;

    @Test
    public void shouldFindNoBookingsIfRepositoryIsEmpty() {
        Iterable<Booking> bookings = repository.findAll();

        assertThat(bookings).isEmpty();
    }

    @Test
    public void shouldStoreBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем","Описание", owner, true));
        Booking booking = repository.save(makeBooking(null, start, end, item, booker, BookingStatus.WAITING));

        assertThat(booking)
                .hasFieldOrPropertyWithValue("startDate", start)
                .hasFieldOrPropertyWithValue("endDate", end)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING)
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker");
        assertThat(booking.getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем");
    }

    @Test
    public void shouldFindAllBookings() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем","Описание", owner, true));
        Booking booking1 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);
        Booking booking2 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);
        Booking booking3 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);

        Iterable<Booking> bookings = repository.findAll();
        assertThat(bookings).hasSize(3);
    }
}
