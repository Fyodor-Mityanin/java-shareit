package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemValidation validation;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl service;

    @Test
    @DisplayName("JUnit test for getAll Item method")
    void givenItemList_whenGetAllItem_thenReturnItemList() {
        //given
        User user = makeUser(1, "Пётр", "ivanov@mail.ru");

        Item item1 = makeItem(1, "Итем","Описание", user, true);
        Item item2 = makeItem(2, "Итем1","Описание1", user, true);

        when(repository.findAllByOwnerIdOrderById(user.getId()))
                .thenReturn(List.of(item1, item2));
        when(bookingRepository.findLastItemBookings(anyList(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findNextItemBookings(anyList(), any()))
                .thenReturn(Collections.emptyList());

        // when
        List<ItemDto> itemList = service.getAllByUserId(user.getId());

        // then
        assertThat(itemList, notNullValue());
        assertThat(itemList.size(), equalTo(2));
    }

    @Test
    @DisplayName("JUnit test for create Item method")
    void givenItemDtoObject_whenSaveItemDto_thenReturnItemObject() {
        //given
        User user = makeUser(1, "Пётр", "ivanov@mail.ru");
        ItemDto itemDto = makeItemDto(1, "Итем","Описание", user.getId(), true);
        Item item = makeItem(1, "Итем","Описание", user, true);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        doNothing()
                .when(validation)
                .validateCreation(itemDto);
        when(repository.save(ItemMapper.toObject(itemDto, user, null)))
                .thenReturn(item);

        // when
        ItemDto savedItemDto = service.create(user.getId(), itemDto);

        // then
        assertThat(savedItemDto.getId(), notNullValue());
        assertThat(savedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(savedItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }
}