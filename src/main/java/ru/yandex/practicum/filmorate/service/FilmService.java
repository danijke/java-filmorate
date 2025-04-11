package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final RatingService ratingService;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final FilmStorage filmStorage;

    public Film get(Long filmId) {
        return filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("фильм с id = " + filmId + " не найден"));
    }

    public Film create(Film film) {
        log.trace("создание фильма: {}", film);
        validateFilm(film);
        return filmStorage.saveFilm(film)
                .orElseThrow(() -> new NotFoundException("ошибка при сохранении фильма"));
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) throw new ValidationException("id должен быть указан");
        validateFilm(newFilm);
        return filmStorage.updateFilm(newFilm)
                .orElseThrow(() -> new NotFoundException("фильм с id = " + newFilm.getId() + " не найден"));
    }

    public void delete(Long filmId) {
        filmStorage.removeFilm(filmId);
    }

    public Collection<Film> findAll() {
        return filmStorage.getFilms();
    }

    public void setLike(Long filmId, Long userId) {
        filmStorage.setLike(filmId, userId);
        log.info("пользователь {} лайкнул фильм {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.deleteLike(filmId, userId);
        log.info("пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public Collection<Film> getPopular(int count, Long genreId, Integer year) {
        if (count <= 0) throw new ParameterNotValidException("count", "размер выборки должен быть больше нуля");
        return filmStorage.getPopular(count).stream()
                .filter(film -> genreId == null || film.getGenres().stream()
                        .anyMatch(genre -> Objects.equals(genre.getId(), genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .toList();
    }

    public Collection<Film> getDirectorsFilms(Long directorId, String sortBy) {
        return filmStorage.getSortFilms(directorId, sortBy);

    }

    private void validateFilm(Film film) {
        ratingService.validateMpaId(film.getMpa().getId());
        genreService.validateGenres(film.getGenres());
        directorService.validateDirectors(film.getDirectors());
    }
}
