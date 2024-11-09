package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final static int MAX_LENGHT_OF_DESCRIPTION = 200;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> returnAllFilms() {
        log.trace("Список всех фильмов: " + films.values().size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Фильм " + film.getName() + " добавлен.");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        validate(newFilm);
        if (films.containsKey(newFilm.getId())) {
            Film formerFilm = films.get(newFilm.getId());
            log.debug("Информация о фильме " + formerFilm.getName() + " обновляется.");
            formerFilm.setName(newFilm.getName());
            formerFilm.setDescription(newFilm.getDescription());
            formerFilm.setDuration(newFilm.getDuration());
            formerFilm.setReleaseDate(newFilm.getReleaseDate());
            log.debug("Фильм обновлен.");
            return formerFilm;
        }
        log.error("Фильм с id = " + newFilm.getId() + " не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > MAX_LENGHT_OF_DESCRIPTION) {
            log.error("Описание фильма не может превышать 200 знаков.");
            throw new ValidationException("Описание фильма не может превышать 200 знаков.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Слишком ранняя дата релиза.");
            throw new ValidationException("Слишком ранняя дата релиза.");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
