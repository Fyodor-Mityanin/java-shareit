package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.ObjectMaker.*;

@DataJpaTest
public class CommentBookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    CommentRepository repository;

    @Test
    public void shouldFindByItemIdWithAuthor() {

    }

    @Test
    public void shouldStoreBooking() {
        User owner = entityManager.persist(makeUser(null, "Пётр", "ivanov@mail.ru"));
        User commentator = entityManager.persist(makeUser(null, "Иван", "petrov@mail.ru"));
        Item item = entityManager.persist(makeItem(null, "Итем", "Описание", owner, true));
        entityManager.persist(makeComment("коментарий", item, commentator));

        List<CommentDto> comments = repository.findByItemIdWithAuthor(item.getId());

        assertThat(comments)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("text", "коментарий");
    }
}
