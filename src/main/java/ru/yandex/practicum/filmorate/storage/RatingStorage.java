package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface RatingStorage {
    Optional<Rating> findRatingById(Long id);

    Collection<Rating> getAll();
}
