package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface GenreStorage {
    Collection<Genre> findGenresByFilmId(Film film);

    void saveFilmGenres(Long filmId, List<Genre> genres);

    Optional<Genre> findGenreById(Long id);

    Collection<Genre> getAll();
}
