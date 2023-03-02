package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ObjectMaker.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ShareItApp.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(3)
public class BookingIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public BookingIntegrationTest() {
    }

    @Test
    @Order(1)
    public void itemUpdateAvailableTest() throws Exception {
        ItemDto itemDto = makeItemDto(null, null, false);
        mvc.perform(patch("/items/2")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(2)
    public void bookingCreateItemUnavailableTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.plusDays(1), now.plusDays(2));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    public void itemUpdateUnavailable4Test() throws Exception {
        ItemDto itemDto = makeItemDto(null, null, true);
        mvc.perform(patch("/items/2")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(4)
    public void bookingCreateByWrongUserIdTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.plusDays(1), now.plusDays(2));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void bookingCreateByWrongItemIdTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(200, now.plusDays(1), now.plusDays(2));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void bookingCreateEndInPastTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.plusDays(1), now.minusDays(1));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void bookingCreateEndBeforeStartTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.plusDays(2), now.plusDays(1));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    public void bookingCreateStartInPastTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(2, now.minusDays(1), now.plusDays(1));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    public void bookingCreateTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusSeconds(3);
        LocalDateTime end = now.plusSeconds(4);
        long userId = 1;
        long itemId = 2;
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(itemId, start, end);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(userId), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemId), Long.class));
    }

    @Test
    @Order(10)
    public void bookingSetApproveTest() throws Exception {
        long userId = 4;
        long bookerId = 1;
        long bookingId = 1;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.booker.id", is(bookerId), Long.class));
    }

    @Test
    @Order(11)
    public void bookingCreate2Test() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);
        long userId = 1;
        long itemId = 2;
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(itemId, start, end);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(userId), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemId), Long.class));
    }

    @Test
    @Order(12)
    public void bookingGetTest() throws Exception {
        long userId = 1;
        long bookingId = 2;
        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(userId), Long.class));
    }

    @Test
    @Order(12)
    public void bookingGetByOwnerTest() throws Exception {
        long userId = 4;
        long bookerId = 1;
        long bookingId = 2;
        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(bookerId), Long.class));
    }

    @Test
    @Order(13)
    public void bookingGetAllByWrongUserTest() throws Exception {
        long userId = 100;
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    public void bookingGetAllByWrongOwnerUserTest() throws Exception {
        long userId = 100;
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    public void bookingGetAllByUserTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(16)
    public void bookingGetAllByUserAllTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(17)
    public void bookingGetAllByUserFutureTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(18)
    public void bookingGetAllByUserWrongStateTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("state", "UNSUPPORTED_STATUS")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(19)
    public void bookingGetAllByOwnerTest() throws Exception {
        long userId = 4;
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(20)
    public void bookingGetAllByOwnerAllTest() throws Exception {
        long userId = 4;
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(21)
    public void bookingGetAllByOwnerFutureTest() throws Exception {
        long userId = 4;
        mvc.perform(get("/bookings/owner")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class));
    }

    @Test
    @Order(22)
    public void bookingGetAllByOwnerWrongStateTest() throws Exception {
        long userId = 4;
        mvc.perform(get("/bookings/owner")
                        .param("state", "UNSUPPORTED_STATUS")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(23)
    public void userCreateTest() throws Exception {
        UserDto userDto = makeUserDto("other", "other@other.com");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(anything()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @Order(24)
    public void bookingGetByOtherTest() throws Exception {
        long userId = 5;
        long bookingId = 1;
        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(25)
    public void bookingChangeStatusWithoutBodyTest() throws Exception {
        long userId = 5;
        long bookingId = 2;
        mvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(26)
    public void bookingChangeStatusByBookerTest() throws Exception {
        long userId = 1;
        long bookingId = 2;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(27)
    public void bookingSetApproveByOwnerTest() throws Exception {
        long userId = 4;
        long bookingId = 2;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @Order(28)
    public void bookingChangeStatusAfterApproveTest() throws Exception {
        long userId = 4;
        long bookingId = 2;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(29)
    public void bookingCreateYourOwnItemTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(2);
        LocalDateTime end = now.plusDays(3);
        long userId = 1;
        long itemId = 1;
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(itemId, start, end);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(30)
    public void bookingCreate3Test() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(1).plusHours(1);
        long userId = 4;
        long itemId = 1;
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(itemId, start, end);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class));
    }

    @Test
    @Order(31)
    public void bookingSetRejectTest() throws Exception {
        long userId = 1;
        long bookingId = 3;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    @Order(32)
    public void itemGetWithBookingsTest() throws Exception {
        long itemId = 2;
        long userId = 4;
        Thread.sleep(4000);
        mvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.lastBooking.id", is(1L), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(2L), Long.class));
    }

    @Test
    @Order(33)
    public void itemGetWithoutBookingsTest() throws Exception {
        long itemId = 2;
        long userId = 1;
        mvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(nullValue())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())));
    }

    @Test
    @Order(34)
    public void bookingCreate4Test() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusSeconds(3);
        LocalDateTime end = now.plusDays(1);
        long userId = 1;
        long itemId = 3;
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(itemId, start, end);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4L), Long.class));
    }

    @Test
    @Order(35)
    public void bookingGetAllByUserWaitingTest() throws Exception {
        long userId = 1;
        String state = "WAITING";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is(state)));
    }

    @Test
    @Order(36)
    public void bookingGetAllByOwnerWaitingTest() throws Exception {
        long userId = 4;
        String state = "WAITING";
        mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is(state)));
    }

    @Test
    @Order(37)
    public void bookingSetReject2Test() throws Exception {
        long userId = 4;
        long bookingId = 4;
        mvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    @Order(38)
    public void bookingGetAllByUserRejectedTest() throws Exception {
        long userId = 1;
        String state = "REJECTED";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is(state)));
    }

    @Test
    @Order(39)
    public void bookingGetAllByOwnerRejectedTest() throws Exception {
        long userId = 4;
        String state = "REJECTED";
        mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is(state)));
    }

    @Test
    @Order(39)
    public void bookingGetAllByUserWithPagination00Test() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(40)
    public void bookingGetAllByOwnerWithPagination00Test() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(41)
    public void bookingGetAllByUserWithPaginationNegativeFromTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("from", "-1")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(42)
    public void bookingGetAllByOwnerWithPaginationNegativeFromTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings/owner")
                        .param("from", "-1")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(43)
    public void bookingGetAllByUserWithPaginationNegativeSizeTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(44)
    public void bookingGetAllByOwnerWithPaginationNegativeSizeTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(45)
    public void bookingGetAllByUserWithPaginationTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings")
                        .param("from", "2")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(46)
    public void bookingGetAllByOwnerWithPaginationTest() throws Exception {
        long userId = 1;
        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(47)
    public void bookingGetAllByBookerCurrentTest() throws Exception {
        long userId = 1;
        String state = "CURRENT";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(48)
    public void bookingGetAllByBookerPastTest() throws Exception {
        long userId = 1;
        String state = "PAST";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(49)
    public void bookingGetAllByBookerCurrentPaginationTest() throws Exception {
        long userId = 1;
        String state = "CURRENT";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(50)
    public void bookingGetAllByBookerPastPaginationTest() throws Exception {
        long userId = 1;
        String state = "PAST";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(51)
    public void bookingGetAllByBookerFuturePaginationTest() throws Exception {
        long userId = 1;
        String state = "FUTURE";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(52)
    public void bookingGetAllByBookerWaitingPaginationTest() throws Exception {
        long userId = 1;
        String state = "WAITING";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(53)
    public void bookingGetAllByBookerRejectedPaginationTest() throws Exception {
        long userId = 1;
        String state = "REJECTED";
        mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}