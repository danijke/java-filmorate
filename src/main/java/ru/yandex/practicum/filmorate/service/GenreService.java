package ru.yandex.practicum.filmorate.service;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;


}
