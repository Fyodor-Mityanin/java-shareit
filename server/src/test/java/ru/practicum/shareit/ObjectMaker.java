package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ObjectMaker {
    public static UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    public static UserDto makeUserDto(long id, String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    public static User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public static ItemRequest makeItemRequest(long id, String description, User user, List<Item> items) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequester(user);
        itemRequest.setItems(items);
        return itemRequest;
    }

    public static Item makeItem(Long id, String name, String description, User user, boolean isAvailable) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(user);
        item.setIsAvailable(isAvailable);
        return item;
    }

    public static Booking makeBooking(
            Long id,
            LocalDateTime start,
            LocalDateTime end,
            Item item,
            User user,
            BookingStatus status
    ) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);
        return booking;
    }

    public static BookingRequestDto makeBookingRequestDto(long itemId, LocalDateTime start, LocalDateTime end) {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(itemId);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        return bookingRequestDto;
    }

    public static ItemDto makeItemDto(long id, String name, String description, long userId, boolean isAvailable) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setOwner(userId);
        itemDto.setAvailable(isAvailable);
        return itemDto;
    }

    public static Comment makeComment(String text, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }
}
