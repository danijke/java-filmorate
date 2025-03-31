package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Component
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    GenreStorage genreStorage;
    RatingStorage ratingStorage;
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Film> saveFilm(Film film) {
        String query = """
                INSERT INTO films (name, description, rating_id, duration, release_date)
                VALUES (?, ?, ?, ?, ?)
                """;
        return save(query,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getDuration(),
                film.getReleaseDate()
        ).map(filmId -> {
            genreStorage.saveFilmGenres(filmId, film.getGenres());
            return findFilmById(filmId);
        }).orElseThrow(() -> new NotSavedException("ошибка при сохранении фильма в БД"));
    }

    @Override
    public void removeFilm(Long id) {
        String q = "DELETE FROM films WHERE film_id = ?";
        if(update(q,id)) {
            throw new NotSavedException(
                    String.format("ошибка при удалении фильма с id = %d", id)
                    );
        }
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return findOne("SELECT * FROM films WHERE film_id = ?", id)
                .map(this::completeFilmEntity);
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany("SELECT * FROM films")
                .stream()
                .map(this::completeFilmEntity)
                .toList();
    }

    @Override
    public Optional<Film> updateFilm(Film newFilm) {
        String q = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        if (!exists(q,newFilm.getId())) {
            return Optional.empty();
        }

        String query = """
                UPDATE films SET
                name = ?, description = ?, rating_id = ?, duration = ?, release_date = ?
                WHERE film_id = ?
                """;

        boolean saved = update(query,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getMpa().getId(),
                newFilm.getDuration(),
                newFilm.getReleaseDate()
                );
        if (!saved) {
             throw new NotSavedException("ошибка при обновлении фильма в БД");
        }
        return Optional.of(newFilm);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String query = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       r.rating_id,
                       r.rating_name,
                       f.duration,
                       f.release_date,
                       COUNT(user_id) likes
                FROM film_likes fl
                JOIN films f ON fl.film_id = f.film_id
                JOIN rating r ON f.rating_id = r.rating_id
                GROUP BY fl.film_id
                ORDER BY likes DESC
                LIMIT (?)
                """;
        return findMany(query,count);
    }

    @Override
    public void setLike(Long filmId, Long userId) {
        String query = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        if (!update(query, filmId, userId)) {
            throw new NotSavedException(
                    String.format("ошибка при сохранении лайка пользователя %s у фильма %s",userId, filmId
                    ));
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        if (!update(query, filmId, userId)) {
            throw new NotSavedException(
                    String.format("ошибка при удалении лайка пользователя %s у фильма %s",userId, filmId
                    ));
        }
    }

    private Film completeFilmEntity(Film film) {
       film.setMpa(ratingStorage.findRatingById(film.getMpa().getId())
               .orElseThrow(() -> new NotFoundException("mpa рейтинг не найден для фильма с id: " + film.getId())));
       film.setGenres(new ArrayList<>(genreStorage.findGenresByFilmId(film)));
       return film;
    }
}
