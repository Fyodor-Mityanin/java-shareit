package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exeptions.UserDuplicateEmailException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long add(User user) {
        String sql = "INSERT INTO USERS (NAME, EMAIL) VALUES (?, ?)";
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                return ps;
            }, gkh);
            return Objects.requireNonNull(gkh.getKey()).longValue();
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage());
            throw new UserDuplicateEmailException(String.format("Почта %s уже есть в базе", user.getEmail()));
        }
    }

    public User updateUser(User user) {
        String sql = "UPDATE USERS SET NAME=?, EMAIL=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getId());
        log.info("updateUser: {} строк обновлено", rowNum);
        return user;
    }

    public void updateEmail(Long id, String email) {
        String sql = "UPDATE USERS SET EMAIL=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, email, id);
        log.info("updateEmail: {} строк обновлено", rowNum);
    }

    public void updateName(Long id, String name) {
        String sql = "UPDATE USERS SET NAME=? WHERE ID=?";
        int rowNum = jdbcTemplate.update(sql, name, id);
        log.info("updateName: {} строк обновлено", rowNum);
    }

    public List<User> getAll() {
        log.info("Поиск всех юзеров");
        String sql = "SELECT U.* FROM USERS U";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    public boolean containsId(Long id) {
        return getUserById(id).isPresent();
    }

    public boolean containsEmail(String email) {
        return getUserByEmail(email).isPresent();
    }

    public Optional<User> getUserById(Long id) {
        String sql = "SELECT U.* FROM USERS U WHERE U.ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByEmail(String email) {
        log.info("Поиск юзера по email={}", email);
        String sql = "SELECT U.*\n" +
                "FROM USERS U\n" +
                "WHERE U.EMAIL = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeUser, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("ID"))
                .email(rs.getString("EMAIL"))
                .name(rs.getString("NAME"))
                .build();
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM USERS U WHERE U.ID = ?";
        jdbcTemplate.update(sql, id);
    }
}
