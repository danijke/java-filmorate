package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;

public interface RatingStorage {
    Optional<Rating> findRatingById(Long id);

    Collection<Rating> getAll();

    boolean isMpaExits(Long id);
}
