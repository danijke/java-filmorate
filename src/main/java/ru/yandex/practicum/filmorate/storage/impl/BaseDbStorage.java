package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.*;
import java.util.*;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected Collection<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected Optional<Long> save(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKeyAs(Long.class));
    }

    protected void saveMany(String query, Collection<T> entities, ParameterizedPreparedStatementSetter<T> setter) {
        jdbc.batchUpdate(query, entities, entities.size(), setter);
    }

    protected boolean update(String query, Object... params) {
        return jdbc.update(query, params) > 0;
    }

    protected boolean exists(String query, Object... params) {
        return Optional.ofNullable(jdbc.queryForObject(query, Boolean.class, params))
                .orElse(false);
    }

}
