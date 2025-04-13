package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final RatingService ratingService;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public Film get(Long filmId) {
        return filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("фильм с id = " + filmId + " не найден"));
    }

    public Film create(Film film) {
        validateFilmEntity(film);
        return filmStorage.saveFilm(film)
                .orElseThrow(() -> new NotFoundException("ошибка при получении сохраненного фильма из бд"));
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) throw new ValidationException("id должен быть указан");
        validateFilmEntity(newFilm);
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
        saveFeedEvent(userId, "LIKE", "ADD", filmId);
        log.info("Пользователь {} лайкнул фильм {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.deleteLike(filmId, userId);
        saveFeedEvent(userId, "LIKE", "REMOVE", filmId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);

    }

    public Collection<Film> getPopular(int count, Long genreId, Integer year) {
        if (count <= 0) throw new ParameterNotValidException("count", "размер выборки должен быть больше нуля");
        return filmStorage.getPopular(count).stream()
                .filter(film -> genreId == null || film.getGenres().stream()
                        .anyMatch(genre -> genre.getId().equals(genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .toList();
    }

    public Collection<Film> getDirectorsFilms(Long directorId, String sortBy) {
        return filmStorage.getSortFilms(directorId, sortBy);

    }

    public void checkFilmsExist(Long... filmIds) {
        if (!filmStorage.containsFilmsByIds(filmIds)) {
            throw new NotFoundException(
                    String.format("фильмы c id : %s не найдены", Arrays.toString(filmIds))
            );
        }
    }

    private void validateFilmEntity(Film film) {
        ratingService.validateMpaId(film.getMpa().getId());
        genreService.validateGenres(film.getGenres());
        directorService.validateDirectors(film.getDirectors());
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        Set<Film> userFilms = new HashSet<>(filmStorage.getFilmsLikedByUser(userId));
        Set<Film> friendFilms = new HashSet<>(filmStorage.getFilmsLikedByUser(friendId));

        userFilms.retainAll(friendFilms);

        Map<Long, Integer> likesMap = filmStorage.getLikesCountForFilms(
                userFilms.stream().map(Film::getId).collect(Collectors.toSet())
        );

        return userFilms.stream()
                .sorted((f1, f2) -> Integer.compare(
                        likesMap.get(f2.getId()),
                        likesMap.get(f1.getId())
                ))
                .collect(Collectors.toList());
    }

    public Collection<Film> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Поисковой запрос не может быть пустым");
        }

        if (by == null || by.isBlank()) {
            throw new ValidationException("Поле поиска должно быть указано");
        }

        String[] fields = by.toLowerCase().split(",");
        boolean searchByTitle = Arrays.asList(fields).contains("title");
        boolean searchByDirector = Arrays.asList(fields).contains("director");

        if (!searchByTitle && !searchByDirector) {
            throw new ValidationException("Поле поиска должно содержать 'title' или 'director'");
        }

        return filmStorage.searchFilms(query, searchByTitle, searchByDirector);
    }

    private void saveFeedEvent(Long userId, String eventType, String operation, Long entityId) {
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();

        feedService.addEvent(event);
        log.info("Событие сохранено: {}", event);
    }

}
