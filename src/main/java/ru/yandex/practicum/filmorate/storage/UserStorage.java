package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public Collection<User> returnAllUsers();

    public User create(User user);

    public User update(User newUser);

    public boolean doesUserExist(Long userId);

    public User getUserById(Long userId);
}
