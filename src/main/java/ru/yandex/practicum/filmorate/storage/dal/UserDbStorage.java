package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcOperations jdbc;
    private final UserRowMapper userRowMapper;

    @Autowired
    public UserDbStorage(JdbcOperations jdbc, UserRowMapper userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Collection<User> returnAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbc.query(sql, userRowMapper);
        return users;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (user_name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
                    stmt.setString(1, user.getName());
                    stmt.setString(2, user.getEmail());
                    stmt.setString(3, user.getLogin());
                    stmt.setDate(4, Date.valueOf(user.getBirthday()));
                    return stmt;
                }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        User userFromTable = getUserById(user.getId());
        return userFromTable;
    }

    @Override
    public User update(User newUser) {
        String sql = "UPDATE users SET user_name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
        jdbc.update(sql, newUser.getName(), newUser.getEmail(), newUser.getLogin(), Date.valueOf(newUser.getBirthday()), newUser.getId());
        String sqlForQuery = "SELECT * FROM users WHERE user_id = ?";
        User thisUser = jdbc.queryForObject(sqlForQuery, userRowMapper, newUser.getId());
        return thisUser;
    }

    @Override
    public boolean doesUserExist(Long userId) {
        try {
            getUserById(userId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }

    @Override
    public User getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = jdbc.queryForObject(sql, userRowMapper, userId);
        return user;
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        String sql = "SELECT * FROM users WHERE user_id IN " +
                "(SELECT friend_id FROM friendship WHERE user_id = ?) AND user_id IN " +
                "(SELECT friend_id FROM friendship WHERE user_id = ?)";
        List<User> commonFrieds = jdbc.query(sql, userRowMapper, user1Id, user2Id);
        return commonFrieds;
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = ?)";
        List<User> friends = jdbc.query(sql, userRowMapper, userId);
        return friends;
    }

}
