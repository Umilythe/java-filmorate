package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcOperations jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> returnAllFilms() {
        String sql = "SELECT films.*, mpa.mpa_type FROM films "
                + "JOIN mpa ON films.mpa_id = mpa.mpa_id ";
        List<Film> films = jdbc.query(sql, filmRowMapper);
        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (film_name, description, release_date, duration, mpa_id)"
                + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
                    stmt.setString(1, film.getName());
                    stmt.setString(2, film.getDescription());
                    stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                    stmt.setInt(4, film.getDuration());
                    stmt.setInt(5, film.getMpa().getId());
                    return stmt;
                }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        putGenres(film.getGenres(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date =?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbc.update(sql, newFilm.getName(), newFilm.getDescription(), Date.valueOf(newFilm.getReleaseDate()), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        return newFilm;
    }

    @Override
    public boolean doesFilmExists(Long filmId) {
        try {
            getFilmById(filmId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = "SELECT * FROM films "
                + "JOIN mpa ON films.mpa_id = mpa.mpa_id "
                + "WHERE film_id = ?";
        Film film = jdbc.queryForObject(sql, filmRowMapper, filmId);
        film.setGenres(getGenresByFilm(filmId));
        return film;
    }

    private void putGenres(Set<Genre> genres, Film film) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genreExists(genre.getId())) {
                    String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
                    jdbc.update(sql, film.getId(), genre.getId());
                } else {
                    throw new NotFoundException("Жанра с таким ID " + genre.getId() + " не существует.");
                }
            }
        } else {
            throw new NotFoundException("Список жанров пуст");
        }
    }

    private LinkedHashSet<Genre> getGenresByFilm(long filmId) {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        String sql = "select * " +
                "from films_genres AS fg " +
                "JOIN genre AS g ON g.genre_id = fg.genre_id " +
                "where film_id = ? " +
                "ORDER BY genre_id";
        SqlRowSet userRows = jdbc.queryForRowSet(sql, filmId);
        while (userRows.next()) {
            int genre_id = userRows.getInt("genre_id");
            String genre_name = userRows.getString("genre_type");
            Genre newGenre = new Genre();
            newGenre.setId(genre_id);
            newGenre.setName(genre_name);
            genres.add(newGenre);
        }
        return genres;
    }

    @Override
    public List<Film> getTopLikedFilms(int count) {
        String sql = "SELECT films.*, mpa.mpa_type FROM films "
                + "JOIN mpa ON films.mpa_id = mpa.mpa_id "
                + "JOIN likes ON films.film_id = likes.film_id "
                + "GROUP BY likes.film_id "
                + "ORDER BY COUNT(likes.film_id) DESC";
        if (count > returnAllFilms().size()) {
            return jdbc.query(sql, filmRowMapper);
        }
        return jdbc.query(sql.concat(" LIMIT ?"), filmRowMapper, count);
    }

    private boolean genreExists(Integer genreId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE genre_id = ?", Integer.class, genreId);
        return count > 0;
    }
}
