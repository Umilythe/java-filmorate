package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.getGenreById(id);
        log.debug("Жанр с id = " + id + " возвращен пользователю.");
        return genre;
    }

    public Collection<Genre> getAllGenres() {
        Collection<Genre> allGenres = genreStorage.getAllGenres();
        log.debug("Возвращен список жанров.");
        return allGenres;
    }

    public Genre createGenre(Genre genre) {
        Genre addGenre = genreStorage.createGenre(genre);
        log.debug("Жанр с id = " + addGenre.getId() + " добавлен");
        return addGenre;
    }

    public void deleteGenreById(int genreId) {
        genreStorage.deleteGenreById(genreId);
        log.debug("Жанр с id = " + genreId + " удален");
    }
}
