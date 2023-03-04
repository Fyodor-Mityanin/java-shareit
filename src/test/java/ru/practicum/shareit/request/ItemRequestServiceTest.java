package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static ru.practicum.shareit.ObjectMaker.makeItemRequest;
import static ru.practicum.shareit.ObjectMaker.makeUser;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Test
    @DisplayName("JUnit test for getAll ItemRequest method")
    void givenItemRequestList_whenGetAllItemRequest_thenReturnItemRequestList() {
        //given
        User user1 = makeUser(1L, "Пётр", "ivanov@mail.ru");
        User user2 = makeUser(2L, "Пётр2", "ivanov2@mail.ru");

        ItemRequest itemRequest1 = makeItemRequest(1, "Описание", user1, Collections.emptyList());
        ItemRequest itemRequest2 = makeItemRequest(2, "Описание2", user2, Collections.emptyList());

        Mockito
                .when(repository.findByRequester_IdNotOrderByCreatedDesc(Mockito.anyLong(), any()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        // when
        List<ItemRequestDto> itemRequestList = service.findAllExceptRequester(Mockito.anyLong(), any());

        // then
        assertThat(itemRequestList, notNullValue());
        assertThat(itemRequestList.size(), equalTo(2));
    }

    @Test
    @DisplayName("JUnit test for create ItemRequest method")
    void givenRequestDtoObject_whenSaveRequestDto_thenReturnRequestObject() {
        //given
        String description = "описание";

        User user = makeUser(1L, "Пётр", "ivanov@mail.ru");
        ItemRequestRequestDto request = new ItemRequestRequestDto();
        request.setDescription(description);

        ItemRequest itemRequest = makeItemRequest(1, description, user, Collections.emptyList());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(ItemRequestMapper.toObject(request, user))).thenReturn(itemRequest);

        // when
        ItemRequestDto savedItemRequestDto = service.create(request, user.getId());

        // then - verify the output
        assertThat(savedItemRequestDto.getId(), notNullValue());
        assertThat(savedItemRequestDto.getRequester().getId(), equalTo(user.getId()));
        assertThat(savedItemRequestDto.getDescription(), equalTo(request.getDescription()));
    }
}