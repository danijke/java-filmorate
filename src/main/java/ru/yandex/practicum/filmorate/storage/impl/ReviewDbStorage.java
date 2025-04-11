package ru.yandex.practicum.filmorate.storage.impl;

import lombok.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Optional;

@Component
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Optional<Review> saveReview(Review review) {
        return Optional.empty();
    }
}

