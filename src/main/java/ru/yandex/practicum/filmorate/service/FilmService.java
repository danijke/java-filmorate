package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserService userService;

    private final FilmStorage filmStorage;

    public Film create(Film film) {
        filmStorage.add(film);
        log.info("фильм {} успешно добавлен", film.getName());
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }

        Film oldFilm = get(newFilm.getId());

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

    public void setLike(Long filmId, Long userId) {
        User user = userService.get(userId);
        Film film = get(filmId);
        film.setLike(userId);
        log.info("пользователь {} добавил лайк фильму {}", user.getName(), film.getName());
    }

    public Film get(Long filmId) {
        return filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("фильм с id = " + filmId + " не найден"));
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userService.get(userId);
        Film film = get(filmId);
        film.deleteLike(userId);
        log.info("пользователь {} удалил лайк у фильма {}", user.getName(), film.getName());
    }

    public Collection<Film> getByPopularity(int count) {
        if (count <= 0) {
            throw new ParameterNotValidException("count", "Некорректный размер выборки. Размер должен быть больше нуля");
        }

        return filmStorage.getAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed()))
                .limit(count)
                .toList();
    }
}

