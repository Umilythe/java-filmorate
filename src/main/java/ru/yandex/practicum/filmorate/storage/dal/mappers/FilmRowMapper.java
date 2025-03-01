package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));

        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        int mpaId = resultSet.getInt("mpa_id");
        String mpaType = resultSet.getString("mpa_type");
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        mpa.setName(mpaType);
        film.setMpa(mpa);
        return film;
    }
}
