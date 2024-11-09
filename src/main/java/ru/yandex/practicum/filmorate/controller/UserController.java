package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> returnAllUsers() {
        log.trace("Список всех пользователей: " + users.values().size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь " + user.getName() + " добавлен.");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
         if (newUser.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            validate(newUser);
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
    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Email не может быть пустым.");
            throw new ValidationException("Email не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Email должен содержать символ @.");
            throw new ValidationException("Email должен содержать символ @.");
        }
        if (user.getLogin().isBlank() & user.getLogin().contains(" ")) {
            log.error("Логин не может быь пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быь пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Этот день еще не наступил.");
            throw new ValidationException("Этот день еще не наступил.");
        }
    }
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
