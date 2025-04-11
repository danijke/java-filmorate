package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre get(Long id) {
        return genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("жанр по id : %d не найден", id)
                ));
    }

    public Collection<Genre> getAll() {
        return genreStorage.findAll();
    }


    public void validateGenres(List<Genre> genres) {
        if (genres != null) {
            genres.stream()
                    .filter(genre -> genre.getId() > genreStorage.getGenresCount())
                    .findFirst()
                    .ifPresent(genre -> {
                        throw new ValidationException(String.format("нет в бд genre_id: %d", genre.getId()));
                    });
        }
    }
}
