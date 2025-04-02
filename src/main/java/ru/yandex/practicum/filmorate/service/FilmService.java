package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final RatingService ratingService;
    private final GenreService genreService;
    private final FilmStorage filmStorage;

    public Film get(Long filmId) {
        return filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("фильм с id = " + filmId + " не найден"));
    }

    public Film create(Film film) {
        log.trace("запрос на создание фильма : {}", film);
        ratingService.validateMpaId(film.getMpa().getId());
        genreService.validateGenres(film.getGenres());

        return filmStorage.saveFilm(film)
                .map(f -> {
                    log.info("фильм {} успешно добавлен", f.getName());
                    return f;
                })
                .orElseThrow(() -> new NotFoundException("Ошибка при получении сохраненного фильма"));
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }
        ratingService.validateMpaId(newFilm.getMpa().getId());
        genreService.validateGenres(newFilm.getGenres());
        return filmStorage.updateFilm(newFilm)
                .map(film -> {
                    log.info("фильм {} успешно обновлен", film.getName());
                    return film;
                })
                .orElseThrow(() -> new NotFoundException("фильм с id = " + newFilm.getId() + " не найден"));
    }

    public Collection<Film> findAll() {
        return filmStorage.getFilms();
    }

    public void setLike(Long filmId, Long userId) {
        filmStorage.setLike(filmId, userId);
        log.info("пользователь {} добавил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.deleteLike(filmId, userId);
        log.info("пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public Collection<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ParameterNotValidException("count", "Некорректный размер выборки. Размер должен быть больше нуля");
        }

        return filmStorage.getPopular(count);
    }
}

