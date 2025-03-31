package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.*;

public class RatingDbStorage extends BaseDbStorage<Rating> implements RatingStorage {
    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Rating> findRatingById(Long id) {
        return findOne("SELECT * FROM rating WHERE rating_id = ?", id);
    }

    @Override
    public Collection<Rating> findAll() {
        return findMany("SELECT * FROM rating");
    }
}
