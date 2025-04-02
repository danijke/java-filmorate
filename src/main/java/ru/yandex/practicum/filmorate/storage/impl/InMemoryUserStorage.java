package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> saveUser(User user) {
        user.setId(getNextId());
        return Optional.ofNullable(users.put(user.getId(), user));
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> update(User newUser) {
        return Optional.ofNullable(users.get(newUser.getId()))
                .map(oldUser -> {
                    oldUser.setEmail(newUser.getEmail());
                    oldUser.setLogin(newUser.getLogin());
                    oldUser.setName(newUser.getName());
                    oldUser.setBirthday(newUser.getBirthday());
                    return oldUser;
                });
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        return null;
    }

    @Override
    public Collection<User> getJointFriends(Long userId, Long friendId) {
        return null;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
