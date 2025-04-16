package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotSavedException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.*;

@Repository
@Slf4j
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Optional<Review> saveReview(Review review) {
        String q = """
                INSERT INTO reviews (content, is_positive, user_id, film_id)
                VALUES (?, ?, ?, ?)
                """;
        return save(q, review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId())
                .map(this::findReviewById)
                .orElseThrow(
                        () -> new NotSavedException("ошибка при сохранении отзыва в БД")
                );
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        String q = "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id = ?)";
        if (!exists(q, review.getReviewId())) {
            return Optional.empty();
        }

        String query = """
                UPDATE reviews SET
                content = ?, is_positive = ?
                WHERE review_id = ?
                """;

        boolean saved = update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (!saved) throw new NotSavedException("ошибка при обновлении отзыва в БД");

        return findReviewById(review.getReviewId());
    }

    @Override
    public Optional<Review> findReviewById(Long id) {
        return findOne("SELECT * FROM reviews WHERE review_id = ?", id);
    }

    @Override
    public void removeReview(Long id) {
        update("DELETE FROM reviews WHERE review_id = ?", id);
    }

    @Override
    public Collection<Review> getSortedByUseful(Long filmId, int count) {
        StringBuilder q = new StringBuilder("SELECT * FROM reviews");
        List<Object> params = new ArrayList<>();

        if (filmId != null) {
            q.append(" WHERE film_id = ?");
            params.add(filmId);
        }

        q.append(" ORDER BY useful DESC LIMIT ?");
        params.add(count);
        return findMany(q.toString(), params.toArray());
    }

    @Override
    public void addReaction(Long reviewId, Long userId, boolean isLike) {
        upsertReaction(reviewId, userId, isLike);
        updateReviewRating(reviewId);
        log.debug("user : {} поставил like : {} отзыву : {}", userId, isLike, reviewId);
    }

    @Override
    public void deleteReaction(Long reviewId, Long userId, boolean isLike) {
        update("DELETE FROM review_useful WHERE review_id = ? AND user_id = ?", reviewId, userId);
        updateReviewRating(reviewId);
        log.debug("user : {} удалил like : {} отзыву : {}", userId, isLike, reviewId);
    }

    @Override
    public boolean containsReviewsByIds(Long... reviewIds) {
        String q = """
                SELECT COUNT(*) = %d
                FROM reviews
                WHERE review_id IN (%s)
                """;
        return existsMany(q, (Object[]) reviewIds);
    }

    private void upsertReaction(Long reviewId, Long userId, boolean b) {
        String query = """
            MERGE INTO review_useful (review_id, user_id, useful)
            KEY (review_id, user_id)
            VALUES (?, ?, ?)
            """;
        if (!update(query, reviewId, userId, b)) {
            throw new NotSavedException(
                    String.format("ошибка при сохранении лайка/дизлайка пользователя id : %s у отзыва id : %s", userId, reviewId
                    ));
        }
    }

    private void updateReviewRating(Long reviewId) {
        String q = """
                UPDATE reviews
                SET useful = COALESCE((
                   SELECT SUM(CASE WHEN useful = TRUE THEN 1 WHEN useful = FALSE THEN -1 ELSE 0 END)
                   FROM review_useful
                   WHERE review_id = reviews.review_id
                   ), 0)
                WHERE review_id = ?;
                """;
        if (!update(q, reviewId)) {
            throw new NotSavedException(
                    String.format("ошибка при обновлении рейтинга у отзыва id : %s", reviewId));
        }
    }
}

