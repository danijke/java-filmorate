package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    void add(Film film);

    void remove(Long id);

    Optional<Film> get(Long id);

    Collection<Film> getAll();

    Optional<Film> update(Film newFilm);
}
