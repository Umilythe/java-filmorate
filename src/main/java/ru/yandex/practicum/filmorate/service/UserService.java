package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public Collection<User> returnAllUsers() {
        return userStorage.returnAllUsers();
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }
        tellIfUserExists(newUser.getId());
        validate(newUser);
        return userStorage.update(newUser);
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Email не может быть пустым.");
            throw new ValidationException("Email не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Email должен содержать символ @.");
            throw new ValidationException("Email должен содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быь пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быь пустым и содержать пробелы.");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Этот день еще не наступил.");
            throw new ValidationException("Этот день еще не наступил.");
        }
    }

    public void addFriend(Long userId, Long friendId) {
        tellIfUserExists(userId);
        tellIfUserExists(friendId);
        friendshipStorage.addFriend(userId, friendId);
        log.info("Пользователи с id " + userId + " и " + friendId + " успешно добавили друг друга в друзья.");
    }

    public void deleteFriend(Long userId, Long friendId) {
        tellIfUserExists(userId);
        tellIfUserExists(friendId);
        friendshipStorage.deleteFriend(userId, friendId);
        log.info("Пользователи с id " + userId + " и " + friendId + " удалили друг друга из друзей.");
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        tellIfUserExists(user1Id);
        tellIfUserExists(user2Id);
        return userStorage.getCommonFriends(user1Id, user2Id);
    }

    public List<User> getFriends(Long userId) {
        tellIfUserExists(userId);
        return userStorage.getFriends(userId);
    }

    private void tellIfUserExists(Long userId) {
        if (!userStorage.doesUserExist(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

}
