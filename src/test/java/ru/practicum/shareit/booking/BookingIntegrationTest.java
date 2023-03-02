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

//
//    @Test
//    @Order(2)
//    public void itemCreateWithoutAuthTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    @Order(3)
//    public void itemCreateWrongUserTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 100)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Order(4)
//    public void itemCreateWithoutAvailableTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Отвертка", "Аккумуляторная отвертка", null);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(5)
//    public void itemCreateWithEmptyNameTest() throws Exception {
//        ItemDto itemDto = makeItemDto("", "Аккумуляторная отвертка", true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(6)
//    public void itemCreateWithEmptyDescriptionTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Отвертка", null, true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(7)
//    public void itemUpdateTest() throws Exception {
//        ItemDto itemDto = makeItemDto(1, "Дрель+", "Аккумуляторная дрель", false);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(8)
//    public void itemUpdateWithoutAuthTest() throws Exception {
//        ItemDto itemDto = makeItemDto(1, "Дрель", "Простая дрель", false);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    @Order(9)
//    public void itemUpdateWithOtherUserTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", false);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @Order(10)
//    public void itemUpdateAvailableTest() throws Exception {
//        ItemDto itemDto = makeItemDto(null, null, true);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1L), Long.class))
//                .andExpect(jsonPath("$.name", is("Дрель+")))
//                .andExpect(jsonPath("$.description", is("Аккумуляторная дрель")))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(11)
//    public void itemUpdateDescriptionTest() throws Exception {
//        ItemDto itemDto = makeItemDto(null, "Аккумуляторная дрель + аккумулятор", null);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1L), Long.class))
//                .andExpect(jsonPath("$.name", is("Дрель+")))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(true)));
//    }
//
//    @Test
//    @Order(12)
//    public void itemUpdateNameTest() throws Exception {
//        ItemDto itemDto = makeItemDto("Аккумуляторная дрель", null, null);
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1L), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is("Аккумуляторная дрель + аккумулятор")))
//                .andExpect(jsonPath("$.available", is(true)));
//    }
//
//    @Test
//    @Order(13)
//    public void itemGetTest() throws Exception {
//        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        mvc.perform(get("/items/" + itemDto.getId())
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(14)
//    public void itemGetFromOtherUserTest() throws Exception {
//        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        mvc.perform(get("/items/" + itemDto.getId())
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(15)
//    public void itemGetUnknownTest() throws Exception {
//        mvc.perform(get("/items/100")
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Order(16)
//    public void itemCreate2Test() throws Exception {
//        ItemDto itemDto = makeItemDto("Отвертка", "Аккумуляторная отвертка", true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(2L), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(17)
//    public void itemCreate3Test() throws Exception {
//        ItemDto itemDto = makeItemDto("Клей Момент", "Тюбик суперклея марки Момент", true);
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(3L), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(18)
//    public void itemGetAllForUser1Test() throws Exception {
//        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        mvc.perform(get("/items/")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(19)
//    public void itemGetAllForUser4Test() throws Exception {
//        ItemDto itemDto2 = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
//        ItemDto itemDto3 = makeItemDto(3L, "Клей Момент", "Тюбик суперклея марки Момент", true);
//        mvc.perform(get("/items/")
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(itemDto2.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto2.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto2.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto2.getAvailable())))
//                .andExpect(jsonPath("$[1].id", is(itemDto3.getId()), Long.class))
//                .andExpect(jsonPath("$[1].name", is(itemDto3.getName())))
//                .andExpect(jsonPath("$[1].description", is(itemDto3.getDescription())))
//                .andExpect(jsonPath("$[1].available", is(itemDto3.getAvailable())));
//    }
//
//    @Test
//    @Order(20)
//    public void itemSearchTest() throws Exception {
//        ItemDto itemDto1 = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        ItemDto itemDto2 = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
//        mvc.perform(get("/items/search")
//                        .param("text", "аккУМУляторная")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
//                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
//                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
//                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
//                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())));
//    }
//
//    @Test
//    @Order(21)
//    public void itemUpdateAvailable2Test() throws Exception {
//        ItemDto itemDto = makeItemDto(null, null, false);
//        mvc.perform(patch("/items/2")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(2L), Long.class))
//                .andExpect(jsonPath("$.name", is("Отвертка")))
//                .andExpect(jsonPath("$.description", is("Аккумуляторная отвертка")))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(22)
//    public void itemSearch2Test() throws Exception {
//        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        mvc.perform(get("/items/search")
//                        .param("text", "дРелЬ")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(23)
//    public void itemSearch3Test() throws Exception {
//        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
//        mvc.perform(get("/items/search")
//                        .param("text", "аккУМУляторная")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(24)
//    public void itemUpdateAvailable3Test() throws Exception {
//        ItemDto itemDto = makeItemDto(null, null, true);
//        mvc.perform(patch("/items/2")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 4)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(2L), Long.class))
//                .andExpect(jsonPath("$.name", is("Отвертка")))
//                .andExpect(jsonPath("$.description", is("Аккумуляторная отвертка")))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(25)
//    public void itemSearch4Test() throws Exception {
//        ItemDto itemDto = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
//        mvc.perform(get("/items/search")
//                        .param("text", "оТверТ")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    @Order(26)
//    public void itemSearchEmptyTest() throws Exception {
//        mvc.perform(get("/items/search")
//                        .param("text", "")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));
//    }
}