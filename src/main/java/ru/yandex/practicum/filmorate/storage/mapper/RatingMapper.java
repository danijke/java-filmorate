package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.*;

public class RatingMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Rating.builder()
                .id(rs.getLong("rating_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
