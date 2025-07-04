package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    GenreStorage genreStorage;
    RatingStorage ratingStorage;
    DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         GenreStorage genreStorage,
                         RatingStorage ratingStorage,
                         DirectorStorage directorStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Optional<Film> saveFilm(Film film) {
        String query = """
                INSERT INTO films (film_name, description, rating_id, duration, release_date)
                VALUES (?, ?, ?, ?, ?)
                """;
        return save(query,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getDuration(),
                film.getReleaseDate()
        ).map(filmId -> {
            if (film.getGenres() != null) {
                genreStorage.saveFilmGenres(filmId, film.getGenres());
            }
            if (film.getDirectors() != null) {
                directorStorage.saveFilmDirectors(filmId, film.getDirectors());
            }
            return findFilmById(filmId);
        }).orElseThrow(() -> new NotSavedException("ошибка при сохранении фильма в БД"));
    }

    @Override
    public void removeFilm(Long id) {
        String q = "DELETE FROM films WHERE film_id = ?";
        update(q, id);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return findOne("SELECT * FROM films WHERE film_id = ?", id)
                .map(this::setFilmEntity);
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany("SELECT * FROM films")
                .stream()
                .map(this::setFilmEntity)
                .toList();
    }

    @Override
    public Optional<Film> updateFilm(Film newFilm) {
        String q = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        if (!exists(q, newFilm.getId())) {
            return Optional.empty();
        }

        String query = """
                UPDATE films SET
                film_name = ?, description = ?, rating_id = ?, duration = ?, release_date = ?
                WHERE film_id = ?
                """;

        boolean saved = update(query,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getMpa().getId(),
                newFilm.getDuration(),
                newFilm.getReleaseDate(),
                newFilm.getId());

        if (!saved) throw new NotSavedException("ошибка при обновлении фильма в БД");

        genreStorage.updateFilmGenres(newFilm.getId(), newFilm.getGenres());
        directorStorage.updateFilmDirectors(newFilm.getId(), newFilm.getDirectors());

        return findFilmById(newFilm.getId());
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String query = """
                SELECT f.*, COUNT(ufl.user_id) AS likes
                FROM user_film_likes ufl
                RIGHT JOIN films f ON ufl.film_id = f.film_id
                GROUP BY f.film_id
                ORDER BY likes DESC NULLS LAST
                LIMIT ?
                """;
        return findMany(query, count).stream()
                .map(this::setFilmEntity)
                .toList();
    }

    @Override
    public void setLike(Long filmId, Long userId) {
        String query = """
                MERGE INTO user_film_likes
                USING DUAL ON (film_id = ? AND user_id = ?)
                WHEN NOT MATCHED THEN
                INSERT (film_id, user_id)
                VALUES (?, ?)
                """;
        update(query, filmId, userId, filmId, userId);
        log.info("Фильму ID_{} добавлен лайк от пользователя ID_{}", filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM user_film_likes WHERE film_id = ? AND user_id = ?";
        if (!update(query, filmId, userId)) {
            throw new NotFoundException("Лайк не найден или пользователь не существует");
        }
    }

    @Override
    public Collection<Film> getSortFilms(Long directorId, String sortBy) {
        String query = """
                SELECT f.*
                FROM films f
                JOIN films_directors fd ON f.film_id = fd.film_id
                """;

        query = switch (sortBy) {
            case "year" -> query + "WHERE fd.director_id = ? " + "ORDER BY f.release_date ASC";
            case "likes" -> query + """
                    LEFT JOIN user_film_likes ufl on f.film_id = ufl.film_id
                    WHERE fd.director_id = ?
                    GROUP BY f.film_id
                    ORDER BY COUNT(ufl.film_id) DESC
                    """;
            default -> throw new ValidationException("Invalid sort parameter");
        };

        return Optional.of(findMany(query, directorId))
                .filter(films -> !films.isEmpty())
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден или у него нет фильмов"))
                .stream()
                .map(this::setFilmEntity)
                .toList();
    }

    @Override
    public boolean containsFilmsByIds(Long... filmIds) {
        String q = """
                SELECT COUNT(*) = %d
                FROM films
                WHERE film_id IN (%s)
                """;
        return existsMany(q, (Object[]) filmIds);
    }

    @Override
    public Film wrapSetFilmEntity(Film film) {
        return setFilmEntity(film);
    }

    private Film setFilmEntity(Film film) {
        film.setMpa(ratingStorage.findRatingById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("mpa рейтинг не найден для фильма с id: " + film.getId())));
        film.setGenres(new HashSet<>(genreStorage.findGenresByFilmId(film)));
        film.setDirectors(new HashSet<>(directorStorage.findDirectorsByFilmId(film)));
        return film;
    }

    @Override
    public Collection<Film> getFilmsLikedByUser(Long userId) {
        String sql = """
                SELECT f.*
                FROM films f
                JOIN user_film_likes ufl ON f.film_id = ufl.film_id
                WHERE ufl.user_id = ?
                """;
        return findMany(sql, userId).stream()
                .map(this::setFilmEntity)
                .toList();
    }

    @Override
    public Map<Long, Integer> getLikesCountForFilms(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();

        String inSql = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = String.format("""
                    SELECT film_id, COUNT(user_id) AS likes
                    FROM user_film_likes
                    WHERE film_id IN (%s)
                    GROUP BY film_id
                """, inSql);

        return jdbc.query(sql, filmIds.toArray(), rs -> {
            Map<Long, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getLong("film_id"), rs.getInt("likes"));
            }
            return result;
        });
    }

    @Override
    public Collection<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector) {
        if (!searchByTitle && !searchByDirector) return Collections.emptyList();

        String likeQuery = "%" + query.toLowerCase() + "%";
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                    SELECT DISTINCT f.*, COUNT(ufl.user_id) AS likes
                    FROM films f
                    LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
                """);

        if (searchByDirector) {
            sql.append("""
                        LEFT JOIN films_directors fd ON f.film_id = fd.film_id
                        LEFT JOIN directors d ON fd.director_id = d.director_id
                    """);
        }

        sql.append("WHERE ");

        if (searchByTitle && searchByDirector) {
            sql.append("LOWER(f.film_name) LIKE ? OR LOWER(d.director_name) LIKE ?");
            params.add(likeQuery);
            params.add(likeQuery);
        } else if (searchByTitle) {
            sql.append("LOWER(f.film_name) LIKE ?");
            params.add(likeQuery);
        } else {
            sql.append("LOWER(d.director_name) LIKE ?");
            params.add(likeQuery);
        }

        sql.append("""
                GROUP BY f.film_id
                ORDER BY likes DESC NULLS LAST
                """);

        return findMany(sql.toString(), params.toArray()).stream()
                .map(this::setFilmEntity)
                .toList();
    }
}

