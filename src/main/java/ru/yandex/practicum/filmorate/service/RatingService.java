package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<Rating> getAll() {
        return ratingStorage.getAll();
    }

    public Rating getById(Long id) {
        return ratingStorage.findRatingById(id)
                .orElseThrow(() -> new NotFoundException("рейтинг с id = " + id + " не найден"));
    }
}
