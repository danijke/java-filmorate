package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.function.Function;

public interface FilmStorage {
    void add(Film film);

    Optional<Film> get(Long id);

    Collection<Film> getAll();

    Optional<Film> update(Film newFilm);
}
