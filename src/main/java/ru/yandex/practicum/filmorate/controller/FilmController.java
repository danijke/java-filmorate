package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Collection<Film> getByPopularity(@RequestParam(defaultValue = "10") int count) {
        return filmService.getByPopularity(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<String> setLike(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(filmService.setLike(filmId, userId));
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<String> deleteLike(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(filmService.deleteLike(filmId, userId));
    }


}

//todo PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.-
//todo DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.-
//todo GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10