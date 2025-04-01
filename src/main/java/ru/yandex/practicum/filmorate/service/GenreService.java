package ru.yandex.practicum.filmorate.service;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

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
        return genreStorage.getAll();
    }
}
