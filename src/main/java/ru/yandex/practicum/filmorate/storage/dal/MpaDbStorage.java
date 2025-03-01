package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcOperations jdbc;
    private final MpaRowMapper mpaRowMapper;

    @Override
    public Mpa getMpaById(int mpaId) {
        try {
            String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
            Mpa mpa = jdbc.queryForObject(sql, mpaRowMapper, mpaId);
            return mpa;
        } catch (Exception e) {
            throw new NotFoundException("Рейтинг с " + mpaId + " не найден.");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        List<Mpa> mpas = jdbc.query(sql, mpaRowMapper);
        return mpas;
    }

    public boolean mpaExists(Integer mpaId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM mpa WHERE mpa_id = ?", Integer.class, mpaId);
        return count > 0;
    }
}
