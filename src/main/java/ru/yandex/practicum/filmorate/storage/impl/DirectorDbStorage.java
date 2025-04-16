package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotSavedException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.*;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Director> saveDirector(Director director) {
        String query = """
                 INSERT INTO directors (director_name)
                 VALUES (?)
                """;
        return save(query, director.getName())
                .map(this::findDirectorById)
                .orElseThrow(() -> new NotSavedException("Ошибка при сохранении режиссера в БД"));
    }

    @Override
    public void removeDirector(Long id) {
        String query = "DELETE FROM directors WHERE director_id = ?";
        update(query, id);
    }

    @Override
    public Optional<Director> findDirectorById(Long id) {
        String query = "SELECT * FROM directors WHERE director_id = ?";
        return findOne(query, id);
    }

    @Override
    public Collection<Director> getDirectors() {
        String query = "SELECT * FROM directors ORDER BY director_id";
        return findMany(query);
    }

    @Override
    public Optional<Director> updateDirector(Director newDirector) {
        String q = "SELECT EXISTS(SELECT 1 FROM directors WHERE director_id = ?)";
        if (!exists(q, newDirector.getId())) {
            return Optional.empty();
        }

        String query = """
                UPDATE directors SET
                director_name = ?
                WHERE director_id = ?
                """;

        boolean saved = update(query, newDirector.getName(), newDirector.getId());

        if (!saved) {
            throw new NotSavedException("Ошибка при обновлении режиссера в БД");
        }
        return Optional.of(newDirector);
    }

    @Override
    public List<Long> getExistingDirectorsIds() {
        return jdbc.queryForList("SELECT director_id FROM directors", Long.class);
    }

    @Override
    public void saveFilmDirectors(Long filmId, Set<Director> directors) {
        String query = """
                INSERT INTO films_directors (film_id, director_id)
                SELECT ?, ?
                WHERE NOT EXISTS (SELECT 1 FROM films_directors
                                  WHERE film_id = ?
                                  AND director_id = ?);
                """;

        saveMany(query, directors, (ps, director) -> {
            ps.setLong(1, filmId);
            ps.setLong(2, director.getId());
            ps.setLong(3, filmId);
            ps.setLong(4, director.getId());
        });
    }

    @Override
    public void updateFilmDirectors(Long filmId, Set<Director> directors) {
        update("DELETE FROM films_directors WHERE film_id = ?", filmId);
        if (directors != null) saveFilmDirectors(filmId, directors);
    }

    @Override
    public Collection<Director> findDirectorsByFilmId(Film film) {
        String query = """
                SELECT fd.director_id, d.director_name
                FROM films_directors fd
                JOIN directors d ON d.director_id = fd.director_id
                WHERE fd.film_id = ?
                """;
        return findMany(query, film.getId());
    }
}
