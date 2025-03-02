package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcOperations jdbc;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Genre getGenreById(int id) {
        try {
            String sql = "SELECT * FROM genre WHERE genre_id = ?";
            Genre genre = jdbc.queryForObject(sql, genreRowMapper, id);
            return genre;
        } catch (Exception e) {
            throw new NotFoundException("Жанра с таким " + id + " не существует.");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";
        List<Genre> genres = jdbc.query(sql, genreRowMapper);
        return genres;
    }

    @Override
    public Genre createGenre(Genre genre) {
        String sql = "INSERT INTO genre (genre_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"genre_id"});
                    stmt.setString(1, genre.getName());
                    return stmt;
                }, keyHolder);
        genre.setId(keyHolder.getKey().intValue());
        Genre addedGenre = getGenreById(genre.getId());
        return addedGenre;
    }

    @Override
    public void deleteGenreById(int genreId) {
        String sql = "DELETE * FROM genre WHERE genre_id = ?";
        jdbc.update(sql, genreId);
    }

}
