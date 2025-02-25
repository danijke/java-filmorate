package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
