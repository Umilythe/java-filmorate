package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> returnAllFilms() {
        log.trace("Список всех фильмов: " + films.values().size());
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public boolean doesFilmExists(Long filmId) {
        return films.containsKey(filmId);
    }

    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    public List<Film> getTopLikedFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getNumberOfLikes).reversed())
                .limit(count)
                .toList();
    }
}
