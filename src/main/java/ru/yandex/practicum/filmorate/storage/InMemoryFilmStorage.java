package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
    }

    @Override
    public void remove(Long id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> update(Film newFilm) {
        return Optional.ofNullable(films.get(newFilm.getId()))
                .map(oldFilm -> {
                    oldFilm.setName(newFilm.getName());
                    oldFilm.setDescription(newFilm.getDescription());
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                    oldFilm.setDuration(newFilm.getDuration());
                    return oldFilm;
                });
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
