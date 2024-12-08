package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final int MAX_LENGHT_OF_DESCRIPTION = 200;

    public Collection<Film> returnAllFilms() {
        return filmStorage.returnAllFilms();
    }

    public Film create(Film film) {
        validate(film);
        filmStorage.create(film);
        log.debug("Фильм " + film.getName() + " добавлен.");
        return film;
    }

    public Film update(Film newFilm) {
        validate(newFilm);
        return filmStorage.update(newFilm);
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() == null || film.getDescription().length() > MAX_LENGHT_OF_DESCRIPTION) {
            log.error("Описание фильма не может превышать 200 знаков.");
            throw new ValidationException("Описание фильма не может превышать 200 знаков.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Слишком ранняя дата релиза.");
            throw new ValidationException("Слишком ранняя дата релиза.");
        }
        if (film.getDuration() == 0 || film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

    public void addLike(Long filmId, Long userId){
        if (!filmStorage.doesFilmExists(filmId)) {
            log.error("Фильм с id " + filmId + " не найден.");
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        if (!userStorage.doesUserExist(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.addLike(userId);
    }

    public void deleteLike(Long filmId, Long userId){
        if (!filmStorage.doesFilmExists(filmId)) {
            log.error("Фильм с id " + filmId + " не найден.");
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        if (!userStorage.doesUserExist(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.deleteLike(userId);
    }

    public List<Film> getPopularFilms(int count){
        return filmStorage.getTopLikedFilms(count);
    }
}
