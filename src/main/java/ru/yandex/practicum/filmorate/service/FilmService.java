package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film create(Film film) {
        filmStorage.add(film);
        log.info("фильм {} успешно добавлен", film.getName());
        return film;
    }

    public Film update(Film newFilm) {
        Film oldFilm = filmStorage.get(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("фильм с id = " + newFilm.getId() + " не найден"));

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("фильм {} успешно обновлен", oldFilm.getName());
        return oldFilm;
    }


    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }
}

