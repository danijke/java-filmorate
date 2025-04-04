package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> saveFilm(Film film) {
        film.setId(getNextId());
        return Optional.ofNullable(films.put(film.getId(), film));
    }

    @Override
    public void removeFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> updateFilm(Film newFilm) {
        return Optional.ofNullable(films.get(newFilm.getId()))
                .map(oldFilm -> {
                    oldFilm.setName(newFilm.getName());
                    oldFilm.setDescription(newFilm.getDescription());
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                    oldFilm.setDuration(newFilm.getDuration());
                    return oldFilm;
                });
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return List.of();
    }

    @Override
    public void setLike(Long filmId, Long userId) {
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
