package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review review) {
        validateReviewEntity(review);
        return reviewStorage.saveReview(review)
                .orElseThrow(
                        () -> new NotFoundException("ошибка при получении сохраненного отзыва из бд")
                );
    }

    public Review update(Review review) {
        validateReviewEntity(review);
        if (review.getReviewId() == null) throw new ValidationException("id должен быть указан");
        return reviewStorage.updateReview(review)
                .orElseThrow(() -> new NotFoundException("отзыв с id = " + review.getReviewId() + " не найден"));
    }

    public void delete(Long reviewId) {
        reviewStorage.removeReview(reviewId);
    }

    public Review get(Long reviewId) {
        return reviewStorage.findReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("отзыв с id = " + reviewId + " не найден"));
    }

    public Collection<Review> getSortedByUseful(Long filmId, int count) {
        return reviewStorage.getSortedByUseful(filmId, count);
    }

    public void setLike(Long reviewId, Long userId) {
        checkReviewsExists(reviewId);
        reviewStorage.addReaction(reviewId, userId, true);
    }

    public void setDislike(Long reviewId, Long userId) {
        checkReviewsExists(reviewId);
        reviewStorage.addReaction(reviewId, userId, false);
    }

    public void deleteReaction(Long reviewId, Long userId) {
        reviewStorage.deleteReaction(reviewId, userId, true);
    }

    public void checkReviewsExists(Long... reviewIds) {
        if (!reviewStorage.containsReviewsByIds(reviewIds)) {
            throw new NotFoundException(
                    String.format("отзывы c id : %s не найдены", Arrays.toString(reviewIds))
            );
        }
    }

    private void validateReviewEntity(Review review) {
        userService.checkUsersExist(review.getUserId());
        filmService.checkFilmsExist(review.getFilmId());
    }
}
