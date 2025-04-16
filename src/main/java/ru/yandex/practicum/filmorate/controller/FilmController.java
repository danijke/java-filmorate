package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable Long filmId) {
        return filmService.get(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(
            @RequestParam(defaultValue = "1000") int count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getPopular(count, genreId, year);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId) {
        filmService.delete(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void setLike(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorsFilms(@PathVariable Long directorId,
                                              @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getDirectorsFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilm(@RequestParam String query,
                                       @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }
}