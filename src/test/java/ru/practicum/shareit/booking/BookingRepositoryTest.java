package ru.practicum.shareit.booking;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
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
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Booking booking1 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);
        Booking booking2 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);
        Booking booking3 = makeBooking(null, start, end, item, booker, BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);

        Iterable<Booking> bookings = repository.findAll();
        assertThat(bookings).hasSize(3);
    }

    @Test
    public void shouldUpdateStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Booking booking = repository.save(makeBooking(null, start, end, item, booker, BookingStatus.WAITING));

        repository.updateStatus(BookingStatus.APPROVED, booking.getId());
        entityManager.clear();

        Optional<Booking> editedBooking = repository.findById(booking.getId());

        assertThat(editedBooking)
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("id", booking.getId())
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
    }

    @Test
    public void shouldFindCurrentByBookerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(1), now.plusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findCurrentByBookerId(booker.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем");
    }

    @Test
    public void shouldCurrentByOwnerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(1), now.plusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findCurrentByOwnerId(owner.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем");
    }

    @Test
    public void shouldFindPastByBookerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findPastByBookerId(booker.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем");
    }

    @Test
    public void shouldFindPastByOwnerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findPastByOwnerId(owner.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем");
    }

    @Test
    public void shouldFindFutureByBookerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findFutureByBookerId(booker.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем2");
    }

    @Test
    public void shouldFindFutureByOwnerId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item2, booker, BookingStatus.WAITING));

        Sort sort = Sort.by("startDate").descending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        List<Booking> listBookings = repository.findFutureByOwnerId(owner.getId(), LocalDateTime.now(), pageable);

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Итем2");
    }

    @Test
    public void shouldFindByIdAndUserId() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Booking booking = entityManager.persist(makeBooking(null, now.minusDays(1), now.plusDays(1), item, booker, BookingStatus.WAITING));

        Optional<Booking> foundBooking = repository.findByIdAndUserId(booking.getId(), owner.getId());

        AssertionsForClassTypes.assertThat(foundBooking)
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("id", booking.getId());
    }

    @Test
    public void shouldFindCrossBookings() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(3), item, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(4), now.plusDays(6), item, booker, BookingStatus.WAITING));

        List<Booking> listBookings = repository.findCrossBookings(item.getId(), now.plusDays(2), now.plusDays(5));

        assertThat(listBookings)
                .hasSize(2);
    }

    @Test
    public void shouldFindLastItemBookings() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        Item item3 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item4 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        Booking booking1 = entityManager.persist(makeBooking(null, now.minusDays(3), now.minusDays(2), item1, booker, BookingStatus.WAITING));
        Booking booking2 = entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item2, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item3, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.plusDays(2), now.plusDays(3), item4, booker, BookingStatus.WAITING));

        List<Booking> listBookings = repository.findLastItemBookings(List.of(item1.getId(), item2.getId(), item3.getId(), item4.getId()), LocalDateTime.now());

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrPropertyWithValue("id", booking1.getId());

        assertThat(listBookings)
                .element(1)
                .hasFieldOrPropertyWithValue("id", booking2.getId());
    }

    @Test
    public void shouldFindNextItemBookings() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item1 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item2 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        Item item3 = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        Item item4 = entityManager.persist(makeItem(null, "Итем2", "Описание2", owner, true));
        entityManager.persist(makeBooking(null, now.minusDays(3), now.minusDays(2), item1, booker, BookingStatus.WAITING));
        entityManager.persist(makeBooking(null, now.minusDays(2), now.minusDays(1), item2, booker, BookingStatus.WAITING));
        Booking booking3 = entityManager.persist(makeBooking(null, now.plusDays(1), now.plusDays(2), item3, booker, BookingStatus.WAITING));
        Booking booking4 = entityManager.persist(makeBooking(null, now.plusDays(2), now.plusDays(3), item4, booker, BookingStatus.WAITING));

        List<Booking> listBookings = repository.findNextItemBookings(List.of(item1.getId(), item2.getId(), item3.getId(), item4.getId()), LocalDateTime.now());

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrPropertyWithValue("id", booking3.getId());

        assertThat(listBookings)
                .element(1)
                .hasFieldOrPropertyWithValue("id", booking4.getId());
    }
}
