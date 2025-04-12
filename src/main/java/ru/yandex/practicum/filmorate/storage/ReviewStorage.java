package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

public interface ReviewStorage {
    Optional<Review> saveReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> findReviewById(Long id);

    void removeReview(Long id);

    Collection<Review> getSortedByUseful(Long filmId, int count);

    void addReaction(Long filmId, Long userId, boolean isLike);

    void deleteReaction(Long reviewId, Long userId, boolean isLike);

    boolean containsReviewsByIds(Long... reviewIds);
}
