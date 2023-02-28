package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;


import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.ObjectMaker.makeUser;
import static ru.practicum.shareit.ObjectMaker.makeUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserValidation validation;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    @DisplayName("JUnit test for getAll Users method")
    void givenUserList_whenGetAllUser_thenReturnUserList() {
        //given
        User user1 = makeUser(1, "Пётр", "ivanov@mail.ru");
        User user2 = makeUser(2, "Пётр2", "ivanov2@mail.ru");

        Mockito.when(repository.findAll()).thenReturn(List.of(user1,user2));

        // when
        List<UserDto> userList = service.getAll();

        // then
        assertThat(userList, notNullValue());
        assertThat(userList.size(), equalTo(2));

    }

    @Test
    @DisplayName("JUnit test for create User method")
    void givenUserDtoObject_whenSaveUserDto_thenReturnUserObject() {
        //given
        UserDto userDto = makeUserDto("Пётр", "ivanov@mail.ru");
        User user = makeUser(1, "Пётр", "ivanov@mail.ru");
        Mockito.when(repository.save(UserMapper.toObject(userDto))).thenReturn(user);
        Mockito.doNothing().when(validation).validateCreation(userDto);

        // when
        UserDto savedUserDto = service.create(userDto);

        // then - verify the output
        assertThat(savedUserDto.getId(), notNullValue());
        assertThat(savedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }
}