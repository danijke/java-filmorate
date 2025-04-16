package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface GenreStorage {
    Collection<Genre> findGenresByFilmId(Film film);

    void saveFilmGenres(Long filmId, Set<Genre> genres);

    void removeFilmGenres(Long filmId);

    void updateFilmGenres(Long filmId, Set<Genre> genres);

    Optional<Genre> findGenreById(Long id);

    Collection<Genre> findAll();

    Long getGenresCount();
}
