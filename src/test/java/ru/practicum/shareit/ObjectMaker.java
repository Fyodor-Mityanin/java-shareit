package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ObjectMaker {
    public static UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    public static UserDto makeUserDto(long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static User makeUser(long id, String name, String email) {
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

    public static Item makeItem(long id, String name, String description, User user, boolean isAvailable) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(user);
        item.setIsAvailable(isAvailable);
        return item;
    }

    public static Booking makeBooking(
            long id,
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
        return BookingRequestDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
    }

    public static ItemDto makeItemDto(long id, String name, String description, long userId, boolean isAvailable) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .owner(userId)
                .available(isAvailable)
                .build();
    }

    public static ItemDto makeItemDto(String name, String description, Boolean isAvailable) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(isAvailable)
                .build();
    }

    public static ItemDto makeItemDto(long id, String name, String description, Boolean isAvailable) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(isAvailable)
                .build();
    }

    public static CommentRequestDto makeCommentRequestDto(String text) {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText(text);
        return commentRequestDto;
    }

    public static ItemRequestRequestDto makeItemRequestRequestDto(String description) {
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto();
        itemRequestRequestDto.setDescription(description);
        return itemRequestRequestDto;
    }
}
