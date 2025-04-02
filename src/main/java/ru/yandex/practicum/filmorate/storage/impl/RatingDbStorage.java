package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.*;

@Component
public class RatingDbStorage extends BaseDbStorage<Rating> implements RatingStorage {
    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Rating> findRatingById(Long id) {
        return findOne("SELECT * FROM rating WHERE rating_id = ?", id);
    }

    @Override
    public Collection<Rating> getAll() {
        return findMany("SELECT * FROM rating ORDER BY rating_id");
    }

    @Override
    public boolean isMpaExits(Long id) {
        String q = "SELECT EXISTS(SELECT 1 FROM rating WHERE rating_id = ?)";
        return exists(q, id);
    }
}
