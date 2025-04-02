package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Component
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findGenresByFilmId(Film film) {
        String query = """
                SELECT fg.genre_id, g.genre_name
                FROM film_genres fg
                JOIN genres g ON g.genre_id = fg.genre_id
                WHERE fg.film_id = ?
                """;
        return findMany(query, film.getId());
    }

    @Override
    public void saveFilmGenres(Long filmId, List<Genre> genres) {
        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        saveMany(query, genres, (ps, genre) -> {
            ps.setLong(1, filmId);
            ps.setLong(2, genre.getId());
        });
    }

    @Override
    public Optional<Genre> findGenreById(Long id) {
        return findOne("SELECT * FROM genres WHERE genre_id = ?", id);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany("SELECT * FROM genres");
    }

    @Override
    public Long getGenresCount() {
        return jdbc.queryForObject("SELECT COUNT(genre_id) FROM genres", Long.class);
    }


}
