package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> returnAllUsers();

    User create(User user);

    User update(User newUser);

    boolean doesUserExist(Long userId);

    User getUserById(Long userId);

    List<User> getCommonFriends(Long user1Id, Long user2Id);

    List<User> getFriends(Long userId);

}
