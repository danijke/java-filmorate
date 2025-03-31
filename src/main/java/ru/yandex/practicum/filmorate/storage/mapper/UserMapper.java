package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("birthday_date").toLocalDate())
                .build();
    }
}
