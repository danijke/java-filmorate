package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotSavedException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<User> saveUser(User user) {
        checkEmailCollision(user);
        String query = """
                INSERT INTO users (email, login, user_name, birthday_date)
                VALUES (?, ?, ?, ?)
                """;
        return save(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
                ).map(this::findUserById)
                .orElseThrow(() -> new NotSavedException("ошибка при сохранении пользователя в БД"));
    }

    @Override
    public void removeUser(Long id) {
        String q = "DELETE FROM users WHERE user_id = ?";
        if(update(q,id)) {
            throw new NotSavedException(
                    String.format("ошибка при удалении пользователя с id = %d", id)
            );
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return findOne("SELECT * FROM users WHERE user_id = ?",id);
    }

    @Override
    public Collection<User> getAll() {
        return findMany("SELECT * FROM users");
    }

    @Override
    public Optional<User> update(User newUser) {
        String q = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        if (!exists(q, newUser.getId())) {
            return Optional.empty();
        }
        checkEmailCollision(newUser);

        String query = """
                UPDATE users SET
                email = ?, login = ?, user_name = ?, birthday_date = ?
                WHERE user_id = ?
                """;

        boolean saved = update(query,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday()
        );
        if (!saved) {
            throw new NotSavedException("ошибка при обновлении пользователя в БД");
        }
        return Optional.of(newUser);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        String q = """
                INSERT INTO user_friends (user_id, friend_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING;
                """;
        return update(q, userId, friendId);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String q = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?;";
        return update(q, userId, friendId);
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        String q = """
                SELECT u.* FROM users u
                JOIN user_friends uf ON u.user_id = uf.friend_id
                WHERE uf.user_id = ?;
                """;

        return findMany(q,userId);
    }

    @Override
    public Collection<User> getJointFriends(Long userId, Long friendId) {
        String q = """
                SELECT u.* FROM users u
                JOIN user_friends uf1 ON u.user_id = uf1.friend_id
                JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id
                WHERE uf1.user_id = ? AND uf2.user_id = ?;
                """;

        return findMany(q,userId, friendId);
    }

    private void checkEmailCollision(User user) {
        String q = "SELECT EXISTS(SELECT 1 FROM users WHERE email = ? AND user_id <> ?)";
        if (exists(q, user.getEmail(), user.getId())) {
            throw new NotSavedException(
                    String.format("ошибка при сохранении в бд, email : %s уже используется", user.getEmail()
                    );
        }
    }

}
