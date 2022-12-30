package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public ItemRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long add(Item item) {
        String sql = "INSERT INTO ITEMS (NAME, DESCRIPTION, AVAILABLE, OWNER, REQUEST) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBoolean(3, item.getAvailable());
            ps.setLong(4, item.getOwner().getId());
            if (item.getRequest() == null) {
                ps.setNull(5, Types.LONGVARCHAR);
            } else {
                ps.setLong(5, item.getRequest().getId());
            }
            return ps;
        }, gkh);
        return Objects.requireNonNull(gkh.getKey()).longValue();
    }

    public Item update(Item item) {
        String sql = "UPDATE ITEMS SET NAME=?, DESCRIPTION=?, AVAILABLE=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, item.getName(), item.getDescription(), item.getAvailable(), item.getId());
        log.info("update: {} строк обновлено", rowNum);
        return item;
    }

    public Optional<Item> getItemById(Long id) {
        String sql = "SELECT I.*, U.NAME AS U_NAME, U.EMAIL AS U_EMAIL\n" +
                "FROM ITEMS I\n" +
                "LEFT JOIN USERS U ON U.ID = I.OWNER\n" +
                "WHERE I.ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeItem, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Item makeItem(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("OWNER"))
                .name(rs.getString("U_NAME"))
                .email(rs.getString("U_EMAIL"))
                .build();
        return Item.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .available(rs.getBoolean("AVAILABLE"))
                .owner(user)
                .request(null)
                .build();
    }

    public void updateAvailable(Long id, Boolean available) {
        String sql = "UPDATE ITEMS SET AVAILABLE=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, available, id);
        log.info("updateAvailable: {} строк обновлено", rowNum);
    }

    public void updateDescription(Long id, String description) {
        String sql = "UPDATE ITEMS SET DESCRIPTION=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, description, id);
        log.info("updateDescription: {} строк обновлено", rowNum);
    }

    public void updateName(Long id, String name) {
        String sql = "UPDATE ITEMS SET NAME=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, name, id);
        log.info("updateName: {} строк обновлено", rowNum);
    }

    public List<Item> getAllByUserId(Long userId) {
        String sql = "SELECT I.*, U.NAME AS U_NAME, U.EMAIL AS U_EMAIL\n" +
                "FROM ITEMS I\n" +
                "LEFT JOIN USERS U ON U.ID = I.OWNER\n" +
                "WHERE U.ID = ?";
        return jdbcTemplate.query(sql, this::makeItem, userId);
    }

    public List<Item> searchByName(String text) {
        log.info("Поиск по тексту: " + text);
        String sql = String.format("SELECT I.*, U.NAME AS U_NAME, U.EMAIL AS U_EMAIL\n" +
                "FROM ITEMS I\n" +
                "LEFT JOIN USERS U ON U.ID = I.OWNER\n" +
                "WHERE LOWER(I.DESCRIPTION) LIKE LOWER('%s')\n" +
                "AND I.AVAILABLE IS TRUE", "%" + text + "%");
        return jdbcTemplate.query(sql, this::makeItem);
    }
}
