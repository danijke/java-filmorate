package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Optional<Film> saveFilm(Film film);

    void removeFilm(Long id);

    Optional<Film> findFilmById(Long id);

    Collection<Film> getFilms();

    Optional<Film> updateFilm(Film newFilm);

    Collection<Film> getPopular(int count);

    void setLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Film wrapSetFilmEntity(Film film);

    Collection<Film> getSortFilms(Long directorId, String sortBy);

    boolean containsFilmsByIds(Long... filmIds);
}
