package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface UserStorage {
    void add(User user);

    void remove(Long id);

    Optional<User> get(Long id);

    Collection<User> getAll();

    Optional<User> update(User newUser);
}
