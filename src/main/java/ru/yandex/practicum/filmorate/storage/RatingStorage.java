package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Optional;

public interface RatingStorage {
    Optional<Rating> findRatingById(Long id);
}
