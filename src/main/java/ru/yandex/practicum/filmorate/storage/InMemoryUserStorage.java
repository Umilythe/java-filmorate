package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> returnAllUsers() {
        log.trace("Список всех пользователей: " + users.values().size());
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь " + user.getName() + " добавлен.");
        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("Информация о пользователе " + oldUser.getName() + " обновляется.");
            if (newUser.getName() == null) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.debug("Пользователь обновлен.");
            return oldUser;
        }
        log.error("Пользователь с id = " + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        User user1 = users.get(user1Id);
        Set<Long> commonFriendsIds = user1.getFriends();
        User user2 = users.get(user2Id);
        Set<Long> secondUserFriendsIds = user2.getFriends();
        commonFriendsIds.retainAll(secondUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        for (Long id : commonFriendsIds) {
            if (doesUserExist(id)) {
                User friend = users.get(id);
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }

    public List<User> getFriends(Long userId) {
        User user = users.get(userId);
        Set<Long> friendsIds = user.getFriends();
        List<User> friends = new ArrayList<>();
        for (Long id : friendsIds) {
            if (doesUserExist(id)) {
                User friend = users.get(id);
                friends.add(friend);
            }
        }
        return friends;
    }

    @Override
    public boolean doesUserExist(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

}
