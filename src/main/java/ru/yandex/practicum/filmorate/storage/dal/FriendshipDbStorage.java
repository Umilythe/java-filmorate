package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcOperations jdbc;

    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?,?)";
        jdbc.update(sql, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
    }

}
