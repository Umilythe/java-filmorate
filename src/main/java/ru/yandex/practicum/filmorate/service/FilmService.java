package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private static final int MAX_LENGHT_OF_DESCRIPTION = 200;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.mpaStorage = mpaStorage;
    }

    public Collection<Film> returnAllFilms() {
        return filmStorage.returnAllFilms();
    }

    public Film create(Film film) {
        validate(film);
        if (!mpaStorage.mpaExists(film.getMpa().getId())) {
            throw new NotFoundException("Рейтинг с ID " + film.getMpa().getId() + " не найден");
        }
        filmStorage.create(film);
        log.debug("Фильм " + film.getName() + " добавлен.");
        return film;
    }

    public Film update(Film newFilm) {
        if (filmStorage.getFilmById(newFilm.getId()) == null) {
            throw new ValidationException("Фильм с id " + newFilm.getId() + " не существует");
        }
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

    public void addLike(Long filmId, Long userId) {
        tellIfFilmExists(filmId);
        tellIfUserExists(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        tellIfFilmExists(filmId);
        tellIfUserExists(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getTopLikedFilms(count);
    }

    private void tellIfFilmExists(Long filmId) {
        if (!filmStorage.doesFilmExists(filmId)) {
            log.error("Фильм с id " + filmId + " не найден.");
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void tellIfUserExists(Long userId) {
        if (!userStorage.doesUserExist(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }


    public Film returnFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }
}
