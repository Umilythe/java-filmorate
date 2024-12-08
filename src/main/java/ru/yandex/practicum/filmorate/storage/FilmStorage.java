package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> returnAllFilms();

    Film create(Film film);

    Film update(Film newFilm);
   boolean doesFilmExists(Long filmId);

    Film getFilmById(Long filmId);

    List<Film> getTopLikedFilms(int count);
}
